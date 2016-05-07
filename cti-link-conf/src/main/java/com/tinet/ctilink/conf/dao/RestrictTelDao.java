package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.model.RestrictTel;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/29 18:02
 */
@Component
public class RestrictTelDao extends BaseDao<RestrictTel> {
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
        Condition condition = new Condition(RestrictTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<RestrictTel> list = selectByCondition(condition);
        for (RestrictTel restrictTel : list) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.RESTRICT_TEL_ENTERPRISE_ID_TYPE_RESTRICT_TYPE_TEL, restrictTel.getEnterpriseId()
                    , restrictTel.getType(), restrictTel.getRestrictType(), restrictTel.getTel()), restrictTel);
        }
        return true;
    }

    public boolean cleanCache() {

        return true;
    }
}
