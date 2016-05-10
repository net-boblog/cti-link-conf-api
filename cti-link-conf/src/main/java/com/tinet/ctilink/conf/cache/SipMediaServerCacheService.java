package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.SipMediaServer;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/5/9 15:18
 */
@Component
public class SipMediaServerCacheService extends AbstractCacheService<SipMediaServer> {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean reloadCache() {
        Condition condition = new Condition(SipMediaServer.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("active", 1);  //查询激活的
        List<SipMediaServer> sipMediaServerList = selectByCondition(condition);

        Set<String> dbKeySet = new HashSet<>();
        if (sipMediaServerList != null && !sipMediaServerList.isEmpty()) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, CacheKey.SIP_MEDIA_SERVER, sipMediaServerList);

            for (SipMediaServer sipMediaServer : sipMediaServerList) {
                redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.SIP_MEDIA_SERVER_IP_ADDR
                        , sipMediaServer.getIpAddr()), sipMediaServer);
                dbKeySet.add(String.format(CacheKey.SIP_MEDIA_SERVER_IP_ADDR, sipMediaServer.getIpAddr()));
            }
        } else {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, CacheKey.SIP_MEDIA_SERVER);
        }

        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.SIP_MEDIA_SERVER_IP_ADDR
                , "*"));
        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }
        return true;
    }
}
