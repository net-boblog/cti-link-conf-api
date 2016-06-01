package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.model.Trunk;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/10 10:04
 */
@Component
public class TrunkCacheService extends AbstractCacheService<Trunk> {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    public boolean reloadCache() {
        List<Entity> list = entityMapper.list();
        if (list == null || list.isEmpty()) {
            return true;
        }
        Set<String> dbKeySet = new HashSet<>();
        Set<String> dbFirstKeySet = new HashSet<>();
        for (Entity entity : list) {
            reloadCache(entity.getEnterpriseId(), dbKeySet);
            dbFirstKeySet.add(String.format(CacheKey.TRUNK_ENTERPRISE_ID_FIRST, entity.getEnterpriseId()));
        }

        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.TRUNK_NUMBER_TRUNK, "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }

        Set<String> existFirstKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.TRUNK_ENTERPRISE_ID_FIRST, "*"));
        existFirstKeySet.removeAll(dbFirstKeySet);
        if (existFirstKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existFirstKeySet);
        }

        return true;
    }


    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(Trunk.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<Trunk> list = selectByCondition(condition);
        if (list != null && !list.isEmpty()) {
            String key;
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.TRUNK_ENTERPRISE_ID_FIRST, enterpriseId), list.get(0));
            for (Trunk trunk : list) {
                key = String.format(CacheKey.TRUNK_NUMBER_TRUNK, trunk.getNumberTrunk());
                redisService.set(Const.REDIS_DB_CONF_INDEX, key, trunk);
                dbKeySet.add(key);
            }
        }

        return true;
    }
}
