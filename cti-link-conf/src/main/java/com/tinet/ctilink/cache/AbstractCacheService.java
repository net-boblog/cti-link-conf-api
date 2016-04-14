package com.tinet.ctilink.cache;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.Entity;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/4/12 15:59
 */
public abstract class AbstractCacheService<T> extends BaseService<T> {

    @Autowired
    RedisService<T> redisService;

    @Autowired
    EntityService entityService;

    //cache
    public boolean loadCache() {
        ApiResult<List<Entity>> list = entityService.list();
        for (Entity entity : list.getData()) {
            loadCache(entity.getEnterpriseId());
        }
        return true;
    }

    public boolean loadCache(Integer enterpriseId) {
        List<T> list = selectByEnterpriseId(enterpriseId);
        for (T t : list) {
            redisService.set(getCacheKey(t), t);
        }
        return true;
    }

    public boolean cleanCache(List<Entity> entityList) {
        Set<String> existKeySet = redisService.keys(getCleanCacheKeyPrefix());
        Set<String> dbKeySet = new HashSet<>();
        for (Entity entity : entityList) {
            List<T> list = selectByEnterpriseId(entity.getEnterpriseId());
            list.stream().forEach(t-> dbKeySet.add(getCacheKey(t)));
        }

        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(existKeySet);
        }

        return true;
    }

    public boolean refreshCache(Integer enterpriseId) {
        Set<String> existKeySet = redisService.keys(getRefreshCacheKeyPrefix(enterpriseId));

        Set<String> dbKeySet = new HashSet<>();
        List<T> list = selectByEnterpriseId(enterpriseId);
        for (T t : list) {
            String key = getCacheKey(t);
            redisService.set(key, t);
            dbKeySet.add(key);
        }

        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(existKeySet);
        }

        return true;
    }

    protected abstract List<T> selectByEnterpriseId(Integer enterpriseId);

    protected abstract String getCacheKey(T t);

    protected abstract String getCleanCacheKeyPrefix();

    protected abstract String getRefreshCacheKeyPrefix(Integer enterpriseId);
}
