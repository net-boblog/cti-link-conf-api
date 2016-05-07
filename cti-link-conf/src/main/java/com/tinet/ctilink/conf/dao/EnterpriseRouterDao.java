package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.EnterpriseRouter;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/6 14:46
 */
@Repository
public class EnterpriseRouterDao extends BaseDao<EnterpriseRouter> {
    @Autowired
    private EntityDao entityDao;

    @Autowired
    private RedisService redisService;


    public boolean loadCache() {
        List<Entity> list = entityDao.list();
        for (Entity entity : list) {
            loadCache(entity.getEnterpriseId());
        }
        return true;
    }

    public boolean loadCache(Integer enterpriseId) {
        Condition condition = new Condition(EnterpriseRouter.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<EnterpriseRouter> list = selectByCondition(condition);
        if (list != null && !list.isEmpty()) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_ROUTER_ENTERPRISE_ID
                    , enterpriseId), list.get(0));
        }
        return true;
    }

    public boolean cleanCache() {

        return true;
    }
}
