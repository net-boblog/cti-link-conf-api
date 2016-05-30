package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseSetting;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseSettingService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/4/7 17:20
 */
@Service
public class EnterpriseSettingServiceImp extends BaseService<EnterpriseSetting>
        implements CtiLinkEnterpriseSettingService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityDao entityDao;

    @Override
    public ApiResult<EnterpriseSetting> createEnterpriseSetting(EnterpriseSetting enterpriseSetting) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseSetting.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        //validate
        enterpriseSetting.setId(null);
        String name = enterpriseSetting.getName();
        if (StringUtils.isEmpty(name)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[name]不正确");
        }
        enterpriseSetting.setName(SqlUtil.escapeSql(name));

        String value = enterpriseSetting.getValue();
        if (StringUtils.isEmpty(value)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[value]不正确");
        }
        enterpriseSetting.setValue(SqlUtil.escapeSql(value));
        enterpriseSetting.setProperty(SqlUtil.escapeSql(enterpriseSetting.getProperty()));
        //insert
        int count = insertSelective(enterpriseSetting);
        if (count != 1) {
            logger.error("EnterpriseSettingServiceImp.createAgent error, " + enterpriseSetting + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
        } else {
            setRefreshCacheMethod(enterpriseSetting.getEnterpriseId());
            return new ApiResult<>(enterpriseSetting);
        }
    }

    @Override
    public ApiResult updateEnterpriseSetting(EnterpriseSetting enterpriseSetting) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseSetting.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        //validate
        Integer id = enterpriseSetting.getId();
        if (id == null || id <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        String value = enterpriseSetting.getValue();
        if (value == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[value]不正确");
        }
        enterpriseSetting.setValue(SqlUtil.escapeSql(value));
        enterpriseSetting.setProperty(SqlUtil.escapeSql(enterpriseSetting.getProperty()));

        EnterpriseSetting dbSetting = selectByPrimaryKey(enterpriseSetting.getId());
        if (dbSetting == null || !enterpriseSetting.getEnterpriseId().equals(dbSetting.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }
        //update
        enterpriseSetting.setName(dbSetting.getName());
        int count = updateByPrimaryKey(enterpriseSetting);
        if (count != 1) {
            logger.error("EnterpriseSettingServiceImp.update error, " + enterpriseSetting + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }
        setRefreshCacheMethod(enterpriseSetting.getEnterpriseId());
        return new ApiResult<>(enterpriseSetting);
    }

    @Override
    public ApiResult<List<EnterpriseSetting>> listEnterpriseSetting(EnterpriseSetting enterpriseSetting) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseSetting.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        ApiResult<List<EnterpriseSetting>> result = new ApiResult<>();

        List<EnterpriseSetting> list = select(enterpriseSetting.getEnterpriseId());
        if (list != null && list.size() > 0) {
            result.setResult(ApiResult.SUCCESS_RESULT);
            result.setDescription(ApiResult.SUCCESS_DESCRIPTION);
            result.setData(list);
            return result;
        }
        return result;
    }

    @Override
    public ApiResult<EnterpriseSetting> getEnterpriseSetting(EnterpriseSetting enterpriseSetting) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseSetting.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        ApiResult<EnterpriseSetting> result = new ApiResult<>();

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

    protected List<EnterpriseSetting> select(Integer enterpriseId) {
        Condition condition = new Condition(EnterpriseSetting.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        return selectByCondition(condition);
    }

    protected String getKey(EnterpriseSetting enterpriseSetting) {
        return String.format(CacheKey.ENTERPRISE_SETTING_ENTERPRISE_ID_NAME,
                enterpriseSetting.getEnterpriseId(), enterpriseSetting.getName());
    }


    /**
     * 刷新缓存方法
     * @param enterpriseId
     */
    protected void setRefreshCacheMethod(Integer enterpriseId) {
        try {
            Method method = this.getClass().getMethod("refreshCache", Integer.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, enterpriseId);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("EnterpriseSettingServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }

    public boolean refreshCache(Integer enterpriseId) {
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.ENTERPRISE_SETTING_ENTERPRISE_ID_NAME, enterpriseId, "*"));
        Set<String> dbKeySet = new HashSet<>();
        List<EnterpriseSetting> list = select(enterpriseId);
        for (EnterpriseSetting t : list) {
            String key = getKey(t);
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, t);
            dbKeySet.add(key);
        }

        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }

        return true;
    }

}
