package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.PublicMoh;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/4/29 14:14
 */
@Repository
public class PublicMohDao extends BaseDao<PublicMoh> {
    @Autowired
    private RedisService redisService;

    public boolean loadCache() {
        List<PublicMoh> publicMohList = selectAll();
        for (PublicMoh publicMoh : publicMohList) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.PUBLIC_MOH_NAME, publicMoh.getName()), publicMoh);
        }
        return true;
    }

    public boolean cleanCache() {
        Set<String> existKeySet = redisService.keys(Const.REDIS_DB_CONF_INDEX, CacheKey.PUBLIC_MOH_NAME + "*");
        Set<String> dbKeySet = new HashSet<>();
        List<PublicMoh> publicMohList = selectAll();
        for (PublicMoh publicMoh : publicMohList) {
            dbKeySet.add(String.format(CacheKey.PUBLIC_MOH_NAME, publicMoh.getName()));
        }

        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }
}
