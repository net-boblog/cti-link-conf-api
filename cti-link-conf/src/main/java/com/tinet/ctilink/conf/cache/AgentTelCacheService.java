package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.AgentTel;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/9 17:51
 */
@Component
public class AgentTelCacheService extends AbstractCacheService<AgentTel> {
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
                , String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID, "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }


    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(AgentTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("cno, is_bind desc");
        List<AgentTel> list = selectByCondition(condition);
        String key;
        String cno = null;
        List<AgentTel> subList = new ArrayList<>();
        for (AgentTel agentTel : list) {
            if (cno == null) {
                cno = agentTel.getCno();
            }
            if (cno.equals(agentTel.getCno())) {
                subList.add(agentTel);
            }
            if (!cno.equals(agentTel.getCno())) {
                key = String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID_CNO, enterpriseId, cno);
                redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
                dbKeySet.add(key);

                cno = agentTel.getCno();
                subList = new ArrayList<>();
                subList.add(agentTel);
            }
        }
        if (subList.size() > 0) {
            key = String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID_CNO, enterpriseId, cno);
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
            dbKeySet.add(key);
        }
        return true;
    }
}
