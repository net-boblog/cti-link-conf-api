package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.model.TelSet;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/6 14:31
 */
@Repository
public class TelSetDao extends BaseDao<TelSet> {
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
        Condition condition = new Condition(TelSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<TelSet> list = selectByCondition(condition);
        if (list != null && !list.isEmpty()) {
            for (TelSet telSet : list) {
                redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.TEL_SET_ENTERPRISE_ID_TSNO
                        , enterpriseId, telSet.getTsno()), telSet);
            }
        }
        return true;
    }

    public boolean cleanCache() {

        return true;
    }
}
