package com.tinet.ctilink.conf.service.imp;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.mapper.EnterpriseMohVoiceMapper;
import com.tinet.ctilink.conf.mapper.EnterpriseVoiceMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.QueueMapper;
import com.tinet.ctilink.conf.model.EnterpriseMoh;
import com.tinet.ctilink.conf.model.EnterpriseMohVoice;
import com.tinet.ctilink.conf.model.EnterpriseVoice;
import com.tinet.ctilink.conf.model.Queue;
import com.tinet.ctilink.conf.request.MohUpdateRequest;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseMohService;
import com.tinet.ctilink.conf.util.VoiceFile;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/6/2 15:55
 */
@Component
public class EnterpriseMohServiceImp extends BaseService<EnterpriseMoh> implements CtiLinkEnterpriseMohService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private EnterpriseVoiceMapper enterpriseVoiceMapper;

    @Autowired
    private EnterpriseMohVoiceMapper enterpriseMohVoiceMapper;

    @Autowired
    private QueueMapper queueMapper;

    @Override
    public ApiResult update(MohUpdateRequest mohUpdateRequest) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(mohUpdateRequest.getEnterpriseId())) {
            return new ApiResult(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        if (mohUpdateRequest.getIsMoh() != 0 && mohUpdateRequest.getIsMoh() != 1) {
            return new ApiResult(ApiResult.FAIL_RESULT, "参数[isMoh]不正确");
        }

        EnterpriseVoice enterpriseVoice = enterpriseVoiceMapper.selectByPrimaryKey(mohUpdateRequest.getVoiceId());
        if (enterpriseVoice == null) {
            return new ApiResult(ApiResult.FAIL_RESULT, "参数[voiceId]不正确");
        }

        if (mohUpdateRequest.getIsMoh() == 0) {  //取消等待音
            //查询是否存在
            Condition condition = new Condition(EnterpriseMohVoice.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("voiceId", mohUpdateRequest.getVoiceId());
            List<EnterpriseMohVoice> enterpriseMohVoiceList = enterpriseMohVoiceMapper.selectByCondition(condition);
            if (enterpriseMohVoiceList == null || enterpriseMohVoiceList.isEmpty()) {
                return new ApiResult(ApiResult.FAIL_RESULT, "等待音不存在");
            }
            EnterpriseMohVoice enterpriseMohVoice = enterpriseMohVoiceList.get(0);
            //判断等待音是否已经使用
            //queue  musicClass
            Condition condition1 = new Condition(Queue.class);
            Condition.Criteria criteria1 = condition1.createCriteria();
            criteria1.andEqualTo("enterpriseId", mohUpdateRequest.getEnterpriseId());
            criteria1.andEqualTo("musicClass", enterpriseVoice.getPath().substring(0
                    , enterpriseVoice.getPath().lastIndexOf(".")).replaceAll("/", ""));

            List<Queue> queueList = queueMapper.selectByCondition(condition1);
            if (queueList != null && queueList.size() > 0) {
                return new ApiResult(ApiResult.FAIL_RESULT, "等待音使用中, 不可取消");
            }

            //删除
            enterpriseMohVoiceMapper.deleteByPrimaryKey(enterpriseMohVoice.getId());
            deleteByPrimaryKey(enterpriseMohVoice.getMohId());
            VoiceFile.unLinkEnterpriseVoiceMoh(enterpriseVoice.getPath());
        } else {  //设置为等待音
            Condition condition = new Condition(EnterpriseMohVoice.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("voiceId", mohUpdateRequest.getVoiceId());
            int count = enterpriseMohVoiceMapper.selectCountByCondition(condition);
            if (count > 0) {
                return new ApiResult(ApiResult.FAIL_RESULT, "已经是等待音");
            }
            //enterpriseMoh
            EnterpriseMoh enterpriseMoh = new EnterpriseMoh();
            enterpriseMoh.setEnterpriseId(mohUpdateRequest.getEnterpriseId());
            enterpriseMoh.setName(enterpriseVoice.getPath().substring(0
                    , enterpriseVoice.getPath().lastIndexOf(".")).replaceAll("/", ""));
            enterpriseMoh.setDirectory(Const.SOUNDS_MOH_CTI_ABS_PATH + enterpriseVoice.getPath() + "/");
//            enterpriseMoh.setApplication();
//            enterpriseMoh.setDigit();
//            enterpriseMoh.setFormat();
//            enterpriseMoh.setSort();
//            enterpriseMoh.setMode();
            insertSelective(enterpriseMoh);
            VoiceFile.linkEnterpriseVoiceMoh(mohUpdateRequest.getEnterpriseId(),  enterpriseVoice.getPath());

            //enterpriseMohVoice
            EnterpriseMohVoice enterpriseMohVoice = new EnterpriseMohVoice();
            enterpriseMohVoice.setEnterpriseId(mohUpdateRequest.getEnterpriseId());
            enterpriseMohVoice.setMohId(enterpriseMoh.getId());
            enterpriseMohVoice.setVoiceId(enterpriseVoice.getId());

            enterpriseMohVoiceMapper.insertSelective(enterpriseMohVoice);

        }

        return new ApiResult(ApiResult.SUCCESS_RESULT);
    }
}
