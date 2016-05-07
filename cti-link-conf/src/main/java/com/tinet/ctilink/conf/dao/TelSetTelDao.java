package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.model.TelSetTel;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/6 14:31
 */
@Repository
public class TelSetTelDao extends BaseDao<TelSetTel> {
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
        Condition condition = new Condition(TelSetTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("tsno");
        List<TelSetTel> list = selectByCondition(condition);
        if (list == null || list.isEmpty()) {
            return true;
        }

        String tsno = null;
        List<TelSetTel> subList = new ArrayList<>();
        for (TelSetTel telSetTel : list) {
            if (tsno == null) {
                tsno = telSetTel.getTsno();
            }
            if (tsno.equals(telSetTel.getTsno())) {
                subList.add(telSetTel);
            }
            if (!tsno.equals(telSetTel.getTsno())) {
                redisService.set(Const.REDIS_DB_CONF_INDEX
                        , String.format(CacheKey.TEL_SET_TEL_ENTERPRISE_TSNO, enterpriseId
                        , tsno), subList);

                tsno = telSetTel.getTsno();
                subList = new ArrayList<>();
                subList.add(telSetTel);
            }
        }

        if (subList.size() > 0) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.TEL_SET_TEL_ENTERPRISE_TSNO, enterpriseId
                    , tsno), subList);
        }

        return true;
    }

    public boolean cleanCache() {

        return true;
    }
}
