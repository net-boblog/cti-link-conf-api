package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.EnterpriseMoh;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/10 09:44
 */
@Component
public class EnterpriseMohCacheService extends AbstractCacheService<EnterpriseMoh> {
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
        for (Entity entity : list) {
            reloadCache(entity.getEnterpriseId(), dbKeySet);
        }
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.ENTERPRISE_MOH_NAME, "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }


    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(EnterpriseMoh.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<EnterpriseMoh> list = selectByCondition(condition);
        String key;
        for (EnterpriseMoh enterpriseMoh : list) {
            key = String.format(CacheKey.ENTERPRISE_MOH_NAME, enterpriseMoh.getName());
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, enterpriseMoh);
            dbKeySet.add(key);
        }
        return true;
    }
}
