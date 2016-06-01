package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.EnterpriseHangupSet;
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
 * @date 16/5/9 19:34
 */
@Component
public class EnterpriseHangupSetCacheService extends AbstractCacheService<EnterpriseHangupSet> {
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
                , String.format(CacheKey.ENTERPRISE_HANGUP_SET_ENTERPRISE_ID_TYPE.replaceAll("%d", "%s"), "*", "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }

    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(EnterpriseHangupSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("type, sort");
        List<EnterpriseHangupSet> list = selectByCondition(condition);
        if (list == null) {
            return true;
        }

        int type = -1;
        List<EnterpriseHangupSet> subList = new ArrayList<>();
        String key;
        for (EnterpriseHangupSet enterpriseHangupSet : list) {
            if (type == -1) {
                type = enterpriseHangupSet.getType();
            }
            if (type == enterpriseHangupSet.getType()) {
                subList.add(enterpriseHangupSet);
            }
            if (type != enterpriseHangupSet.getType()) {
                key = String.format(CacheKey.ENTERPRISE_HANGUP_SET_ENTERPRISE_ID_TYPE, enterpriseId, type);
                redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
                dbKeySet.add(key);

                type = enterpriseHangupSet.getType();
                subList = new ArrayList<>();
                subList.add(enterpriseHangupSet);
            }
        }

        if (subList.size() > 0) {
            key = String.format(CacheKey.ENTERPRISE_HANGUP_SET_ENTERPRISE_ID_TYPE, enterpriseId, type);
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
            dbKeySet.add(key);
        }

        return true;
    }
}
