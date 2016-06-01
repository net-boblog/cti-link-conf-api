package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EnterpriseIvrMapper;
import com.tinet.ctilink.conf.mapper.EnterpriseMohVoiceMapper;
import com.tinet.ctilink.conf.model.EnterpriseIvr;
import com.tinet.ctilink.conf.model.EnterpriseMohVoice;
import com.tinet.ctilink.conf.model.EnterpriseVoice;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseVoiceService;
import com.tinet.ctilink.conf.util.VoiceFile;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.util.FileUtils;
import com.tinet.ctilink.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/4/7 17:20
 */
@Service
public class EnterpriseVoiceServiceImp extends BaseService<EnterpriseVoice> implements CtiLinkEnterpriseVoiceService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private EnterpriseMohVoiceMapper enterpriseMohVoiceMapper;

    @Autowired
    private EnterpriseIvrMapper enterpriseIvrMapper;

    //http接口
    @Override
    public ApiResult<EnterpriseVoice> createEnterpriseVoice(MultipartFormDataInput input) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        Integer enterpriseId = null;
        List<InputPart> inputParts = uploadForm.get("enterpriseId");
        if (inputParts != null) {
            InputPart inputPart = inputParts.get(0);
            try {
                enterpriseId = Integer.parseInt(inputPart.getBodyAsString());
            } catch (Exception e) {
                logger.error("EnterpriseVoiceServiceImp.createEnterpriseVoice error", e);
            }
        }
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseId)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        inputParts = uploadForm.get("voiceName");
        String voiceName = null;
        if (inputParts != null) {
            InputPart inputPart = inputParts.get(0);
            try {
                voiceName = inputPart.getBodyAsString();
            } catch (IOException e) {
                logger.error("EnterpriseVoiceServiceImp.createEnterpriseVoice error", e);
            }
        }

        if (StringUtils.isEmpty(voiceName)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[voiceName]不正确");
        }

        inputParts = uploadForm.get("description");
        String description = null;
        if (inputParts != null) {
            InputPart inputPart = inputParts.get(0);
            try {
                description = inputPart.getBodyAsString();
            } catch (IOException e) {
                logger.error("EnterpriseVoiceServiceImp.createEnterpriseVoice error", e);
            }
        }

        inputParts = uploadForm.get("file");
        File file = null;
        if (inputParts != null) {
            InputPart inputPart = inputParts.get(0);
            try {
                MultivaluedMap<String, String> header = inputPart.getHeaders();

                file = inputPart.getBody(File.class, null);
            } catch (IOException e) {
                logger.error("EnterpriseVoiceServiceImp.createEnterpriseVoice error", e);
            }
        }

        if (file == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[file]不正确");
        }

        EnterpriseVoice enterpriseVoice = new EnterpriseVoice();
        enterpriseVoice.setEnterpriseId(enterpriseId);
        enterpriseVoice.setVoiceName(voiceName);
        enterpriseVoice.setDescription(description);
        return createEnterpriseVoice(file, enterpriseVoice);
    }

    //dubbo接口调用
    /**
     *
     * @param file 支持file=null, 自助录音时
     * @param enterpriseVoice 语音实体
     * @return
     */
    @Override
    public ApiResult<EnterpriseVoice> createEnterpriseVoice(File file, EnterpriseVoice enterpriseVoice) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseVoice.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (StringUtils.isEmpty(enterpriseVoice.getVoiceName())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[voiceName]不能为空");
        }
        enterpriseVoice.setVoiceName(SqlUtil.escapeSql(enterpriseVoice.getVoiceName()));

        boolean success = false;
        if (file == null) {
            if (enterpriseVoice.getVoiceName().startsWith("[自助录音]")) {
                success = true;
            } else {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[file]不能为空");
            }
        } else {
            long timestamp = new Date().getTime();
            String uploadPath = Const.SOUNDS_IVR_VOICE_ABS_PATH + enterpriseVoice.getEnterpriseId();
            String srcFile = timestamp + "old." + StringUtils.substringAfterLast(file.getName(), ".");
            String destFile = timestamp + ".wav";

            if (VoiceFile.mkDir(uploadPath)) {
                FileUtils.moveFile(file, srcFile, destFile);
                success = VoiceFile.transferEnterpriseVoice(enterpriseVoice.getEnterpriseId(), srcFile, destFile);
            }
            enterpriseVoice.setPath(enterpriseVoice.getEnterpriseId() + "/" + destFile);
            enterpriseVoice.setAuditStatus(3);
            enterpriseVoice.setAuditComment(SqlUtil.escapeSql(enterpriseVoice.getAuditComment()));
            enterpriseVoice.setExpiredHour(SqlUtil.escapeSql(enterpriseVoice.getExpiredHour()));
            enterpriseVoice.setDescription(SqlUtil.escapeSql(enterpriseVoice.getDescription()));
        }

        if (success) {
            int count = insertSelective(enterpriseVoice);
            if (count != 1) {
                logger.error("EnterpriseVoiceServiceImp.createEnterpriseVoice error, " + enterpriseVoice + ", count=" + count);
                return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
            } else {
                return new ApiResult<>(enterpriseVoice);
            }
        } else {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
        }
    }

    @Override
    public ApiResult deleteEnterpriseVoice(EnterpriseVoice enterpriseVoice) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseVoice.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (enterpriseVoice.getId() == null || enterpriseVoice.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        EnterpriseVoice dbEnterpriseVoice = selectByPrimaryKey(enterpriseVoice.getId());
        if (dbEnterpriseVoice == null ||
                !enterpriseVoice.getEnterpriseId().equals(dbEnterpriseVoice.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        if (isUseInMoh(enterpriseVoice.getEnterpriseId(), enterpriseVoice.getId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "已被等待音乐设置，不能删除");
        }
        if (isUseInIvr(enterpriseVoice)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "在语音导航中使用，不能删除");
        }
        boolean success = VoiceFile.deleteEnterpriseVoice(dbEnterpriseVoice.getPath());

        int count = deleteByPrimaryKey(enterpriseVoice.getId());

        if (count != 1) {
            logger.error("EnterpriseVoiceServiceImp.deleteEnterpriseVoice error, " + enterpriseVoice + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "删除失败");
        }
        return new ApiResult(ApiResult.SUCCESS_RESULT);
    }

    @Override
    public ApiResult<EnterpriseVoice> updateEnterpriseVoice(MultipartFormDataInput input) {
        return null;
    }

    @Override
    public ApiResult<EnterpriseVoice> updateEnterpriseVoice(File file, EnterpriseVoice enterpriseVoice) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseVoice.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (enterpriseVoice.getId() == null || enterpriseVoice.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        EnterpriseVoice dbEnterpriseVoice = selectByPrimaryKey(enterpriseVoice.getId());
        if (dbEnterpriseVoice == null ||
                !enterpriseVoice.getEnterpriseId().equals(dbEnterpriseVoice.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        if (isUseInMoh(enterpriseVoice.getEnterpriseId(), enterpriseVoice.getId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "已被等待音乐设置，不能更新");
        }
        if (isUseInIvr(enterpriseVoice)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "在语音导航中使用，不能更新");
        }

        if (StringUtils.isEmpty(enterpriseVoice.getVoiceName())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[voiceName不能为空]");
        }
        dbEnterpriseVoice.setVoiceName(SqlUtil.escapeSql(enterpriseVoice.getVoiceName()));

        long timestamp = new Date().getTime();
        String uploadPath = Const.SOUNDS_IVR_VOICE_ABS_PATH + enterpriseVoice.getEnterpriseId();
        String srcFile = timestamp + "old." + StringUtils.substringAfterLast(file.getName(), ".");
        String destFile = timestamp + ".wav";

        String oldPath = dbEnterpriseVoice.getPath();
        boolean success = false;
        if (VoiceFile.mkDir(uploadPath)) {
            FileUtils.moveFile(file, srcFile, destFile);
            success = VoiceFile.transferEnterpriseVoice(enterpriseVoice.getEnterpriseId(), srcFile, destFile);
        }

        if (success) {
            dbEnterpriseVoice.setAuditStatus(3);
            dbEnterpriseVoice.setAuditComment(SqlUtil.escapeSql(enterpriseVoice.getAuditComment()));
            dbEnterpriseVoice.setExpiredHour(SqlUtil.escapeSql(enterpriseVoice.getExpiredHour()));
            dbEnterpriseVoice.setPath(enterpriseVoice.getEnterpriseId() + "/" + destFile);
            dbEnterpriseVoice.setDescription(SqlUtil.escapeSql(enterpriseVoice.getDescription()));

            int count = updateByPrimaryKeySelective(dbEnterpriseVoice);
            if (count != 1) {
                logger.error("EnterpriseVoiceServiceImp.updateEnterpriseVoice error, " + dbEnterpriseVoice + ", count=" + count);
                return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
            } else {
                VoiceFile.deleteEnterpriseVoice(oldPath);
                return new ApiResult<>(dbEnterpriseVoice);
            }
        } else {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }
    }

    @Override
    public ApiResult<List<EnterpriseVoice>> listEnterpriseVoice(EnterpriseVoice enterpriseVoice) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseVoice.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        Condition condition = new Condition(EnterpriseMohVoice.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseVoice.getEnterpriseId());
        condition.setOrderByClause("id");
        List<EnterpriseVoice> list = selectByCondition(condition);
        return new ApiResult<>(list);
    }

    private boolean isUseInMoh(Integer enterpriseId, Integer voiceId) {
        Condition condition = new Condition(EnterpriseMohVoice.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        criteria.andEqualTo("voiceId", voiceId);
        int count = enterpriseMohVoiceMapper.selectCountByCondition(condition);

        return count > 0;
    }

    private boolean isUseInIvr(EnterpriseVoice enterpriseVoice) {
        Condition condition = new Condition(EnterpriseIvr.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseVoice.getEnterpriseId());
        List<Integer> actionList = new ArrayList<>();
        actionList.add(Const.ENTERPRISE_IVR_OP_ACTION_PLAY);
        actionList.add(Const.ENTERPRISE_IVR_OP_ACTION_SELECT);
        actionList.add(Const.ENTERPRISE_IVR_OP_ACTION_READ);
        actionList.add(Const.ENTERPRISE_IVR_OP_ACTION_DIAL);
        criteria.andIn("action", actionList);
        List<EnterpriseIvr> list = enterpriseIvrMapper.selectByCondition(condition);
        if (list != null && list.size() > 0) {
            String path = enterpriseVoice.getPath().substring(0, enterpriseVoice.getPath().lastIndexOf("."));
            for (EnterpriseIvr enterpriseIvr : list) {
                if (enterpriseIvr.getProperty().contains(path)) {
                    return true;
                }
            }
        }
        return false;
    }

}
