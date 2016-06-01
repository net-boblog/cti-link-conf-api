package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.EnterpriseIvr;
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
 * @date 16/5/10 09:13
 */
@Component
public class EnterpriseIvrCacheService extends AbstractCacheService<EnterpriseIvr> {
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
                , String.format(CacheKey.ENTERPRISE_IVR_ENTERPRISE_ID_IVR_ID.replaceAll("%d", "%s"), "*", "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }

    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(EnterpriseIvr.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("ivr_id");
        List<EnterpriseIvr> list = selectByCondition(condition);
        if (list == null) {
            return true;
        }

        int ivrId = -1;
        List<EnterpriseIvr> subList = new ArrayList<>();
        String key;
        for (EnterpriseIvr enterpriseIvr : list) {
            if (ivrId == -1) {
                ivrId = enterpriseIvr.getIvrId();
            }
            if (ivrId == enterpriseIvr.getIvrId()) {
                subList.add(enterpriseIvr);
            }
            if (ivrId != enterpriseIvr.getIvrId()) {
                key = String.format(CacheKey.ENTERPRISE_IVR_ENTERPRISE_ID_IVR_ID, enterpriseId, ivrId);
                redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
                dbKeySet.add(key);

                ivrId = enterpriseIvr.getIvrId();
                subList = new ArrayList<>();
                subList.add(enterpriseIvr);
            }
        }

        if (subList.size() > 0) {
            key = String.format(CacheKey.ENTERPRISE_IVR_ENTERPRISE_ID_IVR_ID, enterpriseId, ivrId);
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
            dbKeySet.add(key);
        }

        return true;
    }
}
