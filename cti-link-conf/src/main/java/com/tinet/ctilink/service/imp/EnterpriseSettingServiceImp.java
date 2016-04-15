package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseSetting;
import com.tinet.ctilink.service.AbstractService;
import com.tinet.ctilink.service.EnterpriseSettingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:20
 */
@Service
public class EnterpriseSettingServiceImp extends AbstractService<EnterpriseSetting>
        implements EnterpriseSettingService {

    private final static Logger logger = LoggerFactory.getLogger(EnterpriseSettingServiceImp.class);

    private final static String CACHE_KEY_PREFIX = "cti-link.enterprise_setting.";
    private final static String CACHE_KEY = CACHE_KEY_PREFIX + "%d.name.%s";
    private final static String METHOD_REFRESH_CACHE = "refreshCache";


    public ApiResult<EnterpriseSetting> create(EnterpriseSetting enterpriseSetting) {
        ApiResult result = new ApiResult(ApiResult.FAIL_RESULT);
        //validate
        enterpriseSetting.setId(null);
        Integer enterpriseId = enterpriseSetting.getEnterpriseId();
        if (enterpriseId == null || enterpriseId <= 0) {
            result.setDescription("参数[enterpriseId]不正确");
            return result;
        }
        String name = enterpriseSetting.getName();
        if (StringUtils.isEmpty(name)) {
            result.setDescription("参数[name]不正确");
            return result;
        }
        String value = enterpriseSetting.getValue();
        if (StringUtils.isEmpty(value)) {
            result.setDescription("参数[value]不正确");
            return result;
        }

        //insert
        int count = insertSelective(enterpriseSetting);
        System.out.println(enterpriseSetting.getId());
        System.out.println(enterpriseSetting.getCreateTime());
        if (count != 1) {
            result.setDescription("新增失败");
        } else {
            result.setResult(ApiResult.SUCCESS_RESULT);
            result.setData(enterpriseSetting);
        }
        return result;
    }

    public ApiResult update(EnterpriseSetting enterpriseSetting) {
        ApiResult result = new ApiResult();
        //validate
        Integer id = enterpriseSetting.getId();
        if (id == null || id <= 0) {
            result.setDescription("参数[id]不正确");
            return result;
        }
        Integer enterpriseId = enterpriseSetting.getEnterpriseId();
        if (enterpriseId == null || enterpriseId <= 0) {
            result.setDescription("参数[enterpriseId]不正确");
            return result;
        }
        String name = enterpriseSetting.getName();
        if (StringUtils.isEmpty(name)) {
            result.setDescription("参数[name]不正确");
            return result;
        }
        String value = enterpriseSetting.getValue();
        if (StringUtils.isEmpty(value)) {
            result.setDescription("参数[value]不正确");
            return result;
        }
        //update
        int count = updateByPrimaryKeySelective(enterpriseSetting);
        if (count != 1) {
            result.setDescription("更新失败");
            return result;
        } else {
            result.setResult(ApiResult.SUCCESS_RESULT);
            result.setDescription(ApiResult.SUCCESS_DESCRIPTION);
            setAfterReturningMethod(enterpriseId);
        }
        return result;
    }

    public ApiResult<List<EnterpriseSetting>> list(EnterpriseSetting enterpriseSetting) {
        ApiResult<List<EnterpriseSetting>> result = new ApiResult<>();
        //validate
        Integer enterpriseId = enterpriseSetting.getEnterpriseId();
        if (enterpriseId == null || enterpriseId <= 0) {
            result.setDescription("参数[enterpriseId]不正确");
            return result;
        }

        List<EnterpriseSetting> list = selectByEnterpriseId(enterpriseSetting.getEnterpriseId());
        if (list != null && list.size() > 0) {
            result.setResult(ApiResult.SUCCESS_RESULT);
            result.setDescription(ApiResult.SUCCESS_DESCRIPTION);
            result.setData(list);
            return result;
        }
        return result;
    }

    public ApiResult<EnterpriseSetting> get(EnterpriseSetting enterpriseSetting) {
        ApiResult<EnterpriseSetting> result = new ApiResult<>();
        //validate
        Integer enterpriseId = enterpriseSetting.getEnterpriseId();
        if (enterpriseId == null || enterpriseId <= 0) {
            result.setDescription("参数[enterpriseId]不正确");
            return result;
        }

        Integer id = enterpriseSetting.getId();
        if (id == 0 || id <= 0) {
            result.setDescription("参数[id]不正确");
            return result;
        }

        Condition condition = new Condition(EnterpriseSetting.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseSetting.getEnterpriseId());
        criteria.andEqualTo("id", enterpriseSetting.getId());
        List<EnterpriseSetting> list = selectByCondition(condition);
        if (list != null && list.size() > 0) {
            result.setResult(ApiResult.SUCCESS_RESULT);
            result.setDescription(ApiResult.SUCCESS_DESCRIPTION);
            result.setData(list.get(0));
            return result;
        }

        return result;
    }

    public List<EnterpriseSetting> selectByEnterpriseId(Integer enterpriseId) {
        Condition condition = new Condition(EnterpriseSetting.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);

        return selectByCondition(condition);
    }

    private EnterpriseSetting getByName(Integer enterpriseId, String name) {
        Condition condition = new Condition(EnterpriseSetting.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        criteria.andEqualTo("name", name);
        List<EnterpriseSetting> list = selectByCondition(condition);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public String getCacheKey(EnterpriseSetting enterpriseSetting) {
        return String.format(CACHE_KEY, enterpriseSetting.getEnterpriseId(), enterpriseSetting.getName());
    }

    public String getCleanCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "*";
    }

    public String getRefreshCacheKeyPrefix(Integer enterpriseId) {
        return CACHE_KEY_PREFIX + enterpriseId + "*";
    }

}
