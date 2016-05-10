package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.Gateway;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/10 09:51
 */
@Component
public class GatewayCacheService extends AbstractCacheService<Gateway> {
    @Autowired
    private RedisService redisService;

    public boolean reloadCache() {
        List<Gateway> gatewayList = selectAll();
        redisService.set(Const.REDIS_DB_CONF_INDEX, CacheKey.GATEWAY, gatewayList);
        Set<String> dbKeySet = new HashSet<>();
        for (Gateway gateway : gatewayList) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.GATEWAY_NAME
                    , gateway.getName()), gateway);
            dbKeySet.add(String.format(CacheKey.GATEWAY_NAME, gateway.getName()));
        }

        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX, CacheKey.GATEWAY_NAME + ".*");
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }
}
