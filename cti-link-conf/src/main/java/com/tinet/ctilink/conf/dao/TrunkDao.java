package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.model.Trunk;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/6 13:22
 */
@Repository
public class TrunkDao extends BaseDao<Trunk> {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityDao entityDao;

    public boolean loadCache() {
        List<Entity> list = entityDao.list();
        for (Entity entity : list) {
            loadCache(entity.getEnterpriseId());
        }
        return true;
    }

    public boolean loadCache(Integer enterpriseId) {
        Condition condition = new Condition(Trunk.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<Trunk> list = selectByCondition(condition);
        if (list != null && !list.isEmpty()) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.TRUNK_ENTERPRISE_ID_FIRST, enterpriseId)
                    , list.get(0));
            for (Trunk trunk : list) {
                redisService.set(Const.REDIS_DB_CONF_INDEX
                        , String.format(CacheKey.TRUNK_NUMBER_TRUNK, trunk.getNumberTrunk()), trunk);
            }
        }

        return true;
    }
}
