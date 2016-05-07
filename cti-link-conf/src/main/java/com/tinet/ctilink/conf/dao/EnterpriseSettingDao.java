package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.EnterpriseSetting;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/29 19:41
 */
@Component
public class EnterpriseSettingDao extends BaseDao<EnterpriseSetting> {
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
        Condition condition = new Condition(EnterpriseSetting.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<EnterpriseSetting> list = selectByCondition(condition);
        for (EnterpriseSetting enterpriseSetting : list) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.ENTERPRISE_SETTING_ENTERPRISE_ID_NAME, enterpriseSetting.getEnterpriseId()
                    , enterpriseSetting.getName()), enterpriseSetting);
        }
        return true;
    }
}
