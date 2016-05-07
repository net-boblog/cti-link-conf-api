package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.EnterpriseHangupSet;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/6 09:50
 */
@Repository
public class EnterpriseHangupSetDao extends BaseDao<EnterpriseHangupSet> {
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
        Condition condition = new Condition(EnterpriseHangupSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("type, sort");
        List<EnterpriseHangupSet> list = selectByCondition(condition);
        if (list == null) {
            return true;
        }

        int type = -1;
        List<EnterpriseHangupSet> subList = new ArrayList<>();
        for (EnterpriseHangupSet enterpriseHangupSet : list) {
            if (type == -1) {
                type = enterpriseHangupSet.getType();
            }
            if (type == enterpriseHangupSet.getType()) {
                subList.add(enterpriseHangupSet);
            }
            if (type != enterpriseHangupSet.getType()) {
                redisService.set(Const.REDIS_DB_CONF_INDEX
                        , String.format(CacheKey.ENTERPRISE_HANGUP_SET_ENTERPRISE_ID_TYPE, enterpriseId
                        , type), subList);

                type = enterpriseHangupSet.getType();
                subList = new ArrayList<>();
                subList.add(enterpriseHangupSet);
            }
        }

        if (subList.size() > 0) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.ENTERPRISE_HANGUP_SET_ENTERPRISE_ID_TYPE, enterpriseId
                    , type), subList);
        }

        return true;
    }

    public boolean cleanCache() {

        return true;
    }
}
