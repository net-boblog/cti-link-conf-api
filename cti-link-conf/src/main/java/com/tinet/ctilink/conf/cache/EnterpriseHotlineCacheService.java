package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.EnterpriseHotline;
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
 * @date 16/5/10 08:58
 */
@Component
public class EnterpriseHotlineCacheService extends AbstractCacheService<EnterpriseHotline> {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    public boolean reloadCache() {
        List<Entity> list = entityMapper.list();
        Set<String> dbKeySet = new HashSet<>();
        if (list != null) {
            for (Entity entity : list) {
                reloadCache(entity.getEnterpriseId(), dbKeySet);
            }
        }
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.ENTERPRISE_HOTLINE_ENTERPRISE_ID_NUMBER_TRUNK.replaceFirst("%d", "%s"), "*", "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }

    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(EnterpriseHotline.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("is_master desc");
        List<EnterpriseHotline> list = selectByCondition(condition);
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_HOTLINE_ENTERPRISE_ID, enterpriseId), list);
        String key;
        for (EnterpriseHotline enterpriseHotline : list) {
            key = String.format(CacheKey.ENTERPRISE_HOTLINE_ENTERPRISE_ID_NUMBER_TRUNK, enterpriseId, enterpriseHotline.getNumberTrunk());
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, enterpriseHotline);
            dbKeySet.add(key);
        }
        return true;
    }
}
