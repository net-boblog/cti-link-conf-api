package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.Agent;
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
 * @date 16/5/9 17:49
 */
@Component
public class AgentCacheService extends AbstractCacheService<Agent> {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    public boolean reloadCache() {
        List<Entity> list = entityMapper.list();
        Set<String> dbKeySet = new HashSet<>();
        //如果所有企业都销户, 确保还能删除key
        if (list != null) {
            for (Entity entity : list) {
                reloadCache(entity.getEnterpriseId(), dbKeySet);
            }
        }
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.AGENT_ENTERPRISE_ID, "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }


    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(Agent.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<Agent> list = selectByCondition(condition);
        String key;
        for (Agent agent : list) {
            key = String.format(CacheKey.AGENT_ENTERPRISE_ID_CNO, enterpriseId, agent.getCno());
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, agent);
            dbKeySet.add(key);
        }
        return true;
    }
}
