package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.Gateway;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/4/29 13:56
 */
@Repository
public class GatewayDao extends BaseDao<Gateway> {
    @Autowired
    private RedisService redisService;

    public boolean loadCache() {
        List<Gateway> gatewayList = selectAll();
        redisService.set(Const.REDIS_DB_CONF_INDEX, CacheKey.GATEWAY, gatewayList);
        for (Gateway gateway : gatewayList) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.GATEWAY_NAME
                    , gateway.getName()), gateway);
        }
        return true;
    }

    public boolean cleanCache() {
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX, CacheKey.GATEWAY_NAME + ".*");
        Set<String> dbKeySet = new HashSet<>();
        List<Gateway> gatewayList = selectAll();
        for (Gateway gateway : gatewayList) {
            dbKeySet.add(String.format(CacheKey.GATEWAY_NAME, gateway.getName()));
        }

        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }
}
