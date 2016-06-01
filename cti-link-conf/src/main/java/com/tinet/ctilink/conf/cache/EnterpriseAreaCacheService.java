package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.EnterpriseArea;
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
 * @date 16/5/9 19:27
 */
@Component
public class EnterpriseAreaCacheService extends AbstractCacheService<EnterpriseArea> {
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
                , String.format((CacheKey.ENTERPRISE_AREA_ENTERPRISE_ID_GROUP_ID_AREA_CODE.replaceAll("%d", "%s")), "*", "*", "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }


    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(EnterpriseArea.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<EnterpriseArea> list = selectByCondition(condition);
        String key;
        for (EnterpriseArea enterpriseArea : list) {
            key = String.format(CacheKey.ENTERPRISE_AREA_ENTERPRISE_ID_GROUP_ID_AREA_CODE, enterpriseId
                    , enterpriseArea.getGroupId(), enterpriseArea.getAreaCode());
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, enterpriseArea);
            dbKeySet.add(key);
        }
        return true;
    }
}
