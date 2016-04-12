package com.tinet.ctilink.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.cache.AbstractCacheBaseService;
import com.tinet.ctilink.model.EnterpriseSetting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:20
 */
@Service
public class EnterpriseSettingServiceImp extends AbstractCacheBaseService<EnterpriseSetting>
        implements EnterpriseSettingService, InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(EnterpriseSettingServiceImp.class);

    private final static String CACHE_KEY_PREFIX = "cti-link.enterprise_setting.";
    private final static String CACHE_KEY = CACHE_KEY_PREFIX + "%d.name.%s";

    @Override
    public ApiResult create(EnterpriseSetting enterpriseSetting) {
        ApiResult result = new ApiResult();
        //validate
        enterpriseSetting.setId(null);
        Integer enterpriseId = enterpriseSetting.getEnterpriseId();
        if (enterpriseId == null || enterpriseId <= 0) {
            return result.getFailResult("参数[enterpriseId]不正确");
        }
        String name = enterpriseSetting.getName();
        if (StringUtils.isEmpty(name)) {
            return result.getFailResult("参数[name]不正确");
        }

        String value = enterpriseSetting.getValue();
        if (StringUtils.isEmpty(value)) {
            return result.getFailResult("参数[value]不正确");
        }
        //name
        EnterpriseSetting existSetting = getByName(enterpriseId, name);
        if (existSetting != null) {
            return result.getFailResult("参数[name]已存在");
        }

        //insert
        int count = insertSelective(enterpriseSetting);
        if (count != 1) {
            return result.getFailResult("新增失败");
        } else {
            refreshCache(enterpriseId);
        }
        return result;
    }

    @Override
    public ApiResult update(EnterpriseSetting enterpriseSetting) {
        ApiResult result = new ApiResult();
        //validate
        Integer id = enterpriseSetting.getId();
        if (id == null || id <= 0) {
            return result.getFailResult("参数[id]不正确");
        }
        Integer enterpriseId = enterpriseSetting.getEnterpriseId();
        if (enterpriseId == null || enterpriseId <= 0) {
            return result.getFailResult("参数[enterpriseId]不正确");
        }
        String name = enterpriseSetting.getName();
        if (StringUtils.isEmpty(name)) {
            return result.getFailResult("参数[name]不正确");
        }
        String value = enterpriseSetting.getValue();
        if (StringUtils.isEmpty(value)) {
            return result.getFailResult("参数[value]不正确");
        }

        //update
        int count = updateByPrimaryKeySelective(enterpriseSetting);
        if (count != 1) {
            return result.getFailResult("更新失败");
        }
        return result;
    }

    @Override
    public ApiResult<List<EnterpriseSetting>> list(EnterpriseSetting enterpriseSetting) {
        ApiResult<List<EnterpriseSetting>> result = new ApiResult<>();
        //validate
        Integer enterpriseId = enterpriseSetting.getEnterpriseId();
        if (enterpriseId == null || enterpriseId <= 0) {
            return result.getFailResult("参数[enterpriseId]不正确");
        }

        List<EnterpriseSetting> list = selectByEnterpriseId(enterpriseSetting.getEnterpriseId());
        if (list != null && list.size() > 0) {
            result.setData(list);
        }
        return result;
    }

    @Override
    public ApiResult<EnterpriseSetting> get(EnterpriseSetting enterpriseSetting) {
        ApiResult<EnterpriseSetting> result = new ApiResult<>();
        //validate
        Integer enterpriseId = enterpriseSetting.getEnterpriseId();
        if (enterpriseId == null || enterpriseId <= 0) {
            return result.getFailResult("参数[enterpriseId]不正确");
        }

        Integer id = enterpriseSetting.getId();
        if (id == 0 || id <= 0) {
            return result.getFailResult("参数[id]不正确");
        }

        Condition condition = new Condition(EnterpriseSetting.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseSetting.getEnterpriseId());
        criteria.andEqualTo("id", enterpriseSetting.getId());
        List<EnterpriseSetting> list = selectByCondition(condition);
        if (list != null && list.size() > 0) {
            result.setData(list.get(0));
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

    @Override
    public void afterPropertiesSet() throws Exception {
        loadCache();
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
