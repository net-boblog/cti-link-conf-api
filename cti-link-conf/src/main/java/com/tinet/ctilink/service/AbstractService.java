package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.filter.AfterReturningMethod;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.filter.ProviderFilter;
import com.tinet.ctilink.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/4/12 15:59
 *
 * 在BaseService基础上增加了缓存操作的通用方法, 其他service实现abstract方法即可
 */
public abstract class AbstractService<T> extends BaseService<T> {

    @Autowired
    RedisService redisService;

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
            redisService.deleteByKeySet(existKeySet);
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
            redisService.deleteByKeySet(existKeySet);
        }

        return true;
    }

    public boolean setAfterReturningMethod(Integer enterpriseId) {
        try {
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(this.getClass().getMethod("refreshCache", Integer.class)
                    , this, enterpriseId);
            ProviderFilter.methodThreadLocal.set(afterReturningMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    protected abstract List<T> selectByEnterpriseId(Integer enterpriseId);

    protected abstract String getCacheKey(T t);

    protected abstract String getCleanCacheKeyPrefix();

    protected abstract String getRefreshCacheKeyPrefix(Integer enterpriseId);
}
