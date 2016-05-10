package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.SystemSetting;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/9 16:58
 */
@Component
public class SystemSettingCacheService extends AbstractCacheService<SystemSetting> {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean reloadCache() {
        List<SystemSetting> systemSettingList = selectAll();

        Set<String> dbKeySet = new HashSet<>();
        if (systemSettingList != null && !systemSettingList.isEmpty()) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, CacheKey.SYSTEM_SETTING, systemSettingList);

            for (SystemSetting systemSetting : systemSettingList) {
                redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.SYSTEM_SETTING_NAME
                        , systemSetting.getName()), systemSetting);
                dbKeySet.add(String.format(CacheKey.SYSTEM_SETTING_NAME, systemSetting.getName()));
            }
        } else {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, CacheKey.SYSTEM_SETTING);
        }

        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.SYSTEM_SETTING_NAME
                , "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }
}
