package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.EnterpriseClid;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/6 09:43
 */
@Repository
public class EnterpriseClidDao extends BaseDao<EnterpriseClid> {
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
        Condition condition = new Condition(EnterpriseClid.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<EnterpriseClid> list = selectByCondition(condition);
        for (EnterpriseClid enterpriseClid : list) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.ENTERPRISE_CLID_ENTERPRISE_ID, enterpriseClid.getEnterpriseId()), enterpriseClid);
        }
        return true;
    }

    public boolean cleanCache() {

        return true;
    }
}
