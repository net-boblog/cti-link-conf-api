package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.EnterpriseMoh;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/29 14:18
 */
@Repository
public class EnterpriseMohDao extends BaseDao<EnterpriseMoh> {

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
        Condition condition = new Condition(EnterpriseMoh.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<EnterpriseMoh> list = selectByCondition(condition);
        for (EnterpriseMoh enterpriseMoh : list) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.ENTERPRISE_MOH_NAME, enterpriseMoh.getName()), enterpriseMoh);
        }
        return true;
    }

    public boolean cleanCache() {

        return true;
    }

}
