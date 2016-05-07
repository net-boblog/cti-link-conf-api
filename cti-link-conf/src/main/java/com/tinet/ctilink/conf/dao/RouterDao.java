package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.Router;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.List;


/**
 * @author fengwei //
 * @date 16/5/6 14:41
 */
@Repository
public class RouterDao extends BaseDao<Router> {
    @Autowired
    private RedisService redisService;

    public boolean loadCache() {
        Condition condition = new Condition(Router.class);
        condition.setOrderByClause("routerset_id");
        List<Router> list = selectByCondition(condition);
        if (list == null || list.isEmpty()) {
            return true;
        }

        int routerSetId = -1;
        List<Router> subList = new ArrayList<>();
        for (Router router : list) {
            if (routerSetId == -1) {
                routerSetId = router.getRoutersetId();
            }
            if (routerSetId == router.getRoutersetId()) {
                subList.add(router);
            }
            if (routerSetId != router.getRoutersetId()) {
                redisService.set(Const.REDIS_DB_CONF_INDEX
                        , String.format(CacheKey.ROUTER_ROUTERSET_ID, routerSetId), subList);

                routerSetId = router.getRoutersetId();
                subList = new ArrayList<>();
                subList.add(router);
            }
        }

        if (subList.size() > 0) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.ROUTER_ROUTERSET_ID, routerSetId), subList);
        }
        return true;
    }

    public boolean cleanCache() {
        return true;
    }
}
