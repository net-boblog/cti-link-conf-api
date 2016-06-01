package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.model.TelSetTel;
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
 * @date 16/5/10 10:04
 */
@Component
public class TelSetTelCacheService extends AbstractCacheService<TelSetTel> {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityMapper entityMapper;

    public boolean reloadCache() {
        List<Entity> list = entityMapper.list();
        Set<String> dbKeySet = new HashSet<>();
        for (Entity entity : list) {
            reloadCache(entity.getEnterpriseId(), dbKeySet);
        }
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.TEL_SET_TEL_ENTERPRISE_TSNO.replaceFirst("%d", "%s"), "*", "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }

    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(TelSetTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("tsno");
        List<TelSetTel> list = selectByCondition(condition);
        if (list == null) {
            return true;
        }

        String tsno = null;
        List<TelSetTel> subList = new ArrayList<>();
        String key;
        for (TelSetTel telSetTel : list) {
            if (tsno == null) {
                tsno = telSetTel.getTsno();
            }
            if (tsno.equals(telSetTel.getTsno())) {
                subList.add(telSetTel);
            }
            if (!tsno.equals(telSetTel.getTsno())) {
                key = String.format(CacheKey.TEL_SET_TEL_ENTERPRISE_TSNO, enterpriseId, tsno);
                redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
                dbKeySet.add(key);

                tsno = telSetTel.getTsno();
                subList = new ArrayList<>();
                subList.add(telSetTel);
            }
        }

        if (subList.size() > 0) {
            key = String.format(CacheKey.TEL_SET_TEL_ENTERPRISE_TSNO, enterpriseId, tsno);
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
            dbKeySet.add(key);
        }

        return true;
    }
}
