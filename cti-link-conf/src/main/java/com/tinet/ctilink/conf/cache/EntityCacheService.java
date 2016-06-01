package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.Cache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/10 09:50
 */
@Component
public class EntityCacheService extends AbstractCacheService<Entity> {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    public boolean reloadCache() {
        List<Entity> list = entityMapper.list();
        Set<String> dbKeySet = new HashSet<>();
        if (list != null && !list.isEmpty()) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, CacheKey.ENTITY_ENTERPRISE_ACTIVE, list);
            for (Entity entity : list) {
                redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTITY_ENTERPRISE_ID, entity.getEnterpriseId()), entity);
                dbKeySet.add(String.format(CacheKey.ENTITY_ENTERPRISE_ID, entity.getEnterpriseId()));
            }
        }
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.ENTITY_ENTERPRISE_ID.replaceFirst("%d", "%s"), "*"));
        existKeySet.removeAll(dbKeySet);
        existKeySet.remove(CacheKey.ENTITY_ENTERPRISE_ACTIVE);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }
}
