package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.Router;
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
 * @date 16/5/10 10:03
 */
@Component
public class RouterCacheService extends AbstractCacheService<Router> {
    @Autowired
    private RedisService redisService;

    public boolean reloadCache() {
        Condition condition = new Condition(Router.class);
        condition.setOrderByClause("routerset_id");
        List<Router> list = selectByCondition(condition);
        if (list == null) {
            return true;
        }

        int routerSetId = -1;
        List<Router> subList = new ArrayList<>();
        String key;
        Set<String> dbKeySet = new HashSet<>();
        for (Router router : list) {
            if (routerSetId == -1) {
                routerSetId = router.getRoutersetId();
            }
            if (routerSetId == router.getRoutersetId()) {
                subList.add(router);
            }
            if (routerSetId != router.getRoutersetId()) {
                key = String.format(CacheKey.ROUTER_ROUTERSET_ID, routerSetId);
                redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
                dbKeySet.add(key);

                routerSetId = router.getRoutersetId();
                subList = new ArrayList<>();
                subList.add(router);
            }
        }

        if (subList.size() > 0) {
            key = String.format(CacheKey.ROUTER_ROUTERSET_ID, routerSetId);
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
            dbKeySet.add(key);
        }

        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.ROUTER_ROUTERSET_ID.replaceFirst("%d", "%s"), "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }

        return true;
    }
}
