package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.SipMediaServer;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/4/29 14:04
 */
@Component
public class SipMediaServerDao extends BaseDao<SipMediaServer> {
    @Autowired
    private RedisService redisService;

    public boolean loadCache() {
        List<SipMediaServer> gatewayList = selectAll();
        redisService.set(Const.REDIS_DB_CONF_INDEX, CacheKey.GATEWAY, gatewayList);
        for (SipMediaServer gateway : gatewayList) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.GATEWAY_NAME
                    , gateway.getName()), gateway);
        }
        return true;
    }

    public boolean cleanCache() {
        Set<String> existKeySet = redisService.keys(Const.REDIS_DB_CONF_INDEX, CacheKey.GATEWAY + ".name.*");
        Set<String> dbKeySet = new HashSet<>();
        List<SipMediaServer> gatewayList = selectAll();
        for (SipMediaServer gateway : gatewayList) {
            dbKeySet.add(String.format(CacheKey.GATEWAY_NAME, gateway.getName()));
        }

        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }
}
