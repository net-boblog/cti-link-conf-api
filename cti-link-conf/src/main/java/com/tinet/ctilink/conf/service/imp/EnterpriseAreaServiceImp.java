package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.mapper.EnterpriseAreaGroupMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.EnterpriseArea;
import com.tinet.ctilink.conf.model.EnterpriseAreaGroup;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseAreaService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huangbin //
 * @date 2016/4/18. //
 */

@Service
public class EnterpriseAreaServiceImp extends BaseService<EnterpriseArea> implements CtiLinkEnterpriseAreaService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private EnterpriseAreaGroupMapper enterpriseAreaGroupMapper;

    @Override
    public ApiResult<EnterpriseArea> createEnterpriseArea(EnterpriseArea enterpriseArea) {
        if (!entityMapper.validateEntity(enterpriseArea.getEnterpriseId()))
            return new ApiResult<>(ApiResult.FAIL_RESULT, "企业编号不正确");

        ApiResult<EnterpriseArea> result = validateEnterpriseArea(enterpriseArea);
        if (result != null)
            return result;

        enterpriseArea.setCreateTime(new Date());

        int success = insertSelective(enterpriseArea);
        if (success == 1) {
            setRefreshCacheMethod("setCache", enterpriseArea);
            return new ApiResult<>(enterpriseArea);
        }

        logger.error("EnterpriseAreaServiceImp.createEnterpriseArea error " + enterpriseArea + "sueccess" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT, "增加失败");
    }

    @Override
    public ApiResult deleteEnterpriseArea(EnterpriseArea enterpriseArea) {
        if (!entityMapper.validateEntity(enterpriseArea.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT, "企业编号不能为空");

        if (enterpriseArea.getId() == null || enterpriseArea.getId() <= 0)
            return new ApiResult(ApiResult.FAIL_RESULT, "地区组地区id不能为空");

        Condition condition = new Condition(EnterpriseArea.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseArea.getEnterpriseId());
        criteria.andEqualTo("id", enterpriseArea.getId());

        EnterpriseArea enterpriseArea1 = null;
        List<EnterpriseArea> enterpriseAreaList = selectByCondition(condition);
        if (enterpriseAreaList != null && enterpriseAreaList.size() > 0)
            enterpriseArea1 = enterpriseAreaList.get(0);

        int success = deleteByCondition(condition);
        if (success == 1) {
            setRefreshCacheMethod("deleteCache", enterpriseArea1);
            return new ApiResult(ApiResult.SUCCESS_RESULT, ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("EnterpriseAreaServiceImp.deleteEnterpriseArea.deleteEnterpriseArea error " + enterpriseArea + "success" + success);
        return new ApiResult(ApiResult.FAIL_RESULT, "删除失败");

    }

    @Override
    public ApiResult<List<EnterpriseArea>> listEnterpriseArea(EnterpriseArea enterpriseArea) {
        if (!entityMapper.validateEntity(enterpriseArea.getEnterpriseId()))
            return new ApiResult<>(ApiResult.FAIL_RESULT, "企业id不能为空");
        if (enterpriseArea.getGroupId() == null || enterpriseArea.getGroupId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "地区组id不能为空");
        Condition groupCondition = new Condition(EnterpriseAreaGroup.class);
        Condition.Criteria groupCriteria = groupCondition.createCriteria();
        groupCriteria.andEqualTo("id", enterpriseArea.getId());
        groupCriteria.andEqualTo("enterpriseId", enterpriseArea.getEnterpriseId());
        groupCondition.setTableName("cti_link_enterprise_area_group");
        List<EnterpriseAreaGroup> enterpriseAreaGroupList = enterpriseAreaGroupMapper.selectByCondition(groupCondition);
        if (enterpriseAreaGroupList == null || enterpriseAreaGroupList.size() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "地区组id或企业编号bu正确");

        Condition condition = new Condition(EnterpriseArea.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseArea.getEnterpriseId());
        criteria.andEqualTo("groupId", enterpriseArea.getGroupId());
        List<EnterpriseArea> enterpriseAreaList = selectByCondition(condition);

        if (enterpriseAreaList != null && enterpriseAreaList.size() > 0)
            return new ApiResult<>(enterpriseAreaList);
        return new ApiResult<>(ApiResult.FAIL_RESULT, "获取地区列表失败");
    }

    protected String getKey(EnterpriseArea enterpriseArea) {
        return String.format(CacheKey.ENTERPRISE_AREA_ENTERPRISE_ID_GROUP_ID_AREA_CODE, enterpriseArea.getEnterpriseId(),
                enterpriseArea.getGroupId(), enterpriseArea.getAreaCode());
    }

    public void setCache(EnterpriseArea enterpriseArea) {
        redisService.set(Const.REDIS_DB_CONF_INDEX, getKey(enterpriseArea), enterpriseArea);
    }

    public void deleteCache(EnterpriseArea enterpriseArea) {
        redisService.delete(Const.REDIS_DB_CONF_INDEX, getKey(enterpriseArea));
    }

    private void setRefreshCacheMethod(String methodName, EnterpriseArea enterpriseArea) {
        try {
            Method method = this.getClass().getMethod(methodName, EnterpriseArea.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, enterpriseArea);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("EnterpriseAreaServiceImp setRefreshMethod error refresh cache fail class=" +
                    this.getClass().getName());
        }
    }

    private <T> ApiResult<T> validateEnterpriseArea(EnterpriseArea enterpriseArea) {
        if (enterpriseArea.getGroupId() == null || enterpriseArea.getGroupId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "地区组号不正确");
        Condition condition = new Condition(EnterpriseAreaGroup.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("id", enterpriseArea.getId());
        criteria.andEqualTo("enterpriseId", enterpriseArea.getEnterpriseId());
        condition.setTableName("cti_link_enterprise_area_group");
        List<EnterpriseAreaGroup> enterpriseAreaGroupList = enterpriseAreaGroupMapper.selectByCondition(condition);
        if (enterpriseAreaGroupList == null || enterpriseAreaGroupList.size() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "地区组id或企业编号bu正确");

        if (StringUtils.isEmpty(enterpriseArea.getAreaCode()))
            return new ApiResult<>(ApiResult.FAIL_RESULT, "地区区号不能为空");
        Pattern pattern = Pattern.compile(Const.AREA_CODE_VALIDATION);
        Matcher matcher = pattern.matcher(enterpriseArea.getAreaCode().trim());
        if (!matcher.matches())
            return new ApiResult<>(ApiResult.FAIL_RESULT, "地区区号不正确");

        if (StringUtils.isEmpty(enterpriseArea.getProvince()))
            return new ApiResult<>(ApiResult.FAIL_RESULT, "省份不能为空");
        if (StringUtils.isEmpty(enterpriseArea.getCity()))
            return new ApiResult<>(ApiResult.FAIL_RESULT, "城市不能为空");

        return null;
    }
}