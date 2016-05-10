package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.model.EnterpriseVoicemail;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/10 09:51
 */
@Component
public class EnterpriseVoicemailCacheService extends AbstractCacheService<EnterpriseVoicemail> {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityDao entityDao;

    @Override
    public boolean reloadCache() {
        List<Entity> list = entityDao.list();
        if (list == null || list.isEmpty()) {
            return true;
        }
        Set<String> dbKeySet = new HashSet<>();
        for (Entity entity : list) {
            reloadCache(entity.getEnterpriseId(), dbKeySet);
        }
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.ENTERPRISE_VOICEMAIL_ENTERPRISE_ID_ID.replaceAll("%d", "%s"), "*", "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }


    public boolean reloadCache(Integer enterpriseId, Set<String> dbKeySet) {
        Condition condition = new Condition(EnterpriseVoicemail.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<EnterpriseVoicemail> list = selectByCondition(condition);
        String key;
        for (EnterpriseVoicemail enterpriseVoicemail : list) {
            key = String.format(CacheKey.ENTERPRISE_VOICEMAIL_ENTERPRISE_ID_ID, enterpriseId, enterpriseVoicemail.getId());
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, enterpriseVoicemail);
            dbKeySet.add(key);
        }
        return true;
    }
}
