package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.CtiLinkEnterpriseIvrAnchor;
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
 * @date 16/5/16 18:50
 */
@Component
public class EnterpriseIvrAnchorCacheService extends AbstractCacheService<CtiLinkEnterpriseIvrAnchor> {
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
                , String.format(CacheKey.ENTERPRISE_IVR_ANCHOR_ENTERPRISE_ID_IVR_ID.replaceAll("%d", "%s"), "*", "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }

    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(CtiLinkEnterpriseIvrAnchor.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("ivr_id, path");
        List<CtiLinkEnterpriseIvrAnchor> list = selectByCondition(condition);
        if (list == null) {
            return true;
        }

        int ivrId = -1;
        List<CtiLinkEnterpriseIvrAnchor> subList = new ArrayList<>();
        String key;
        for (CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor : list) {
            if (ivrId == -1) {
                ivrId = enterpriseIvrAnchor.getIvrId();
            }
            if (ivrId == enterpriseIvrAnchor.getIvrId()) {
                subList.add(enterpriseIvrAnchor);
            }
            if (!(ivrId == enterpriseIvrAnchor.getIvrId())) {
                key = String.format(CacheKey.ENTERPRISE_IVR_ANCHOR_ENTERPRISE_ID_IVR_ID, enterpriseId, ivrId);
                redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
                dbKeySet.add(key);

                ivrId = enterpriseIvrAnchor.getIvrId();
                subList = new ArrayList<>();
                subList.add(enterpriseIvrAnchor);
            }
        }

        if (subList.size() > 0) {
            key = String.format(CacheKey.ENTERPRISE_IVR_ANCHOR_ENTERPRISE_ID_IVR_ID, enterpriseId, ivrId);
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
            dbKeySet.add(key);
        }

        return true;
    }
}
