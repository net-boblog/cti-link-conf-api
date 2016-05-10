package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.PublicMoh;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/10 10:01
 */
@Component
public class PublicMohCacheService extends AbstractCacheService<PublicMoh> {
    @Autowired
    private RedisService redisService;

    public boolean reloadCache() {
        List<PublicMoh> publicMohList = selectAll();
        Set<String> dbKeySet = new HashSet<>();
        for (PublicMoh publicMoh : publicMohList) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.PUBLIC_MOH_NAME, publicMoh.getName()), publicMoh);
            dbKeySet.add(String.format(CacheKey.PUBLIC_MOH_NAME, publicMoh.getName()));
        }

        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.PUBLIC_MOH_NAME, "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }
}
