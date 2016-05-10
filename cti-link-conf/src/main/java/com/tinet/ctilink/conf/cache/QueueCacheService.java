package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.model.Queue;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/9 17:44
 */
@Component
public class QueueCacheService extends AbstractCacheService<Queue> {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityDao entityDao;

    @Override
    public boolean reloadCache() {
        List<Entity> list = entityDao.list();
        if (list == null || list.isEmpty()) {
            return true;
        }
        Set<String> dbKeySet = new HashSet<>();
        for (Entity entity : list) {
            reloadCache(entity.getEnterpriseId(), dbKeySet);
        }
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.QUEUE_ENTERPRISE_ID, "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }


    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(Queue.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<Queue> list = selectByCondition(condition);
        String key;
        for (Queue queue : list) {
            key = String.format(CacheKey.QUEUE_ENTERPRISE_ID_QNO, enterpriseId, queue.getQno());
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, queue);
            dbKeySet.add(key);
        }
        return true;
    }
}
