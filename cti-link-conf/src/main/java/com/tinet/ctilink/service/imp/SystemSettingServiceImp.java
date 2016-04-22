package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.filter.AfterReturningMethod;
import com.tinet.ctilink.filter.ProviderFilter;
import com.tinet.ctilink.model.SystemSetting;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/4/21 16:36
 */
@Service
public class SystemSettingServiceImp extends BaseService<SystemSetting> implements SystemSettingService {
    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<List<SystemSetting>> listSystemSetting() {
        return null;
    }

    @Override
    public ApiResult<SystemSetting> updateSystemSetting(SystemSetting systemSetting) {

        setRefreshCacheMethod();
        return null;
    }


    public void refreshCache() {
        Set<String> existKeySet = redisService.keys(String.format(CacheKey.SYSTEM_SETTING_NAME, "") + "*");

        Set<String> dbKeySet = new HashSet<>();
        List<SystemSetting> systemSettingList = selectAll();
        //设置全集
        redisService.set(CacheKey.SYSTEM_SETTING, systemSettingList);
        //name
        for (SystemSetting systemSetting : systemSettingList) {
            String key = String.format(CacheKey.SYSTEM_SETTING_NAME, systemSetting.getName());
            redisService.set(key, systemSetting);
            dbKeySet.add(key);
        }

        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.deleteByKeySet(existKeySet);
        }
    }

    protected void setRefreshCacheMethod() {
        try {
            Method method = this.getClass().getMethod("refreshCache");
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this);
            ProviderFilter.methodThreadLocal.set(afterReturningMethod);
        } catch (Exception e) {
//            logger.error("AbstractService.setRefreshCacheMethod error, cache refresh fail, " +
//                    "class=" + this.getClass().getName(), e);
        }
    }
}
