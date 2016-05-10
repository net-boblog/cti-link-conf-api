package com.tinet.ctilink.conf.service;

import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author fengwei //
 * @date 16/4/12 15:59
 *
 * 在BaseService基础上增加了缓存操作的通用方法, 其他service实现abstract方法即可
 */
public abstract class AbstractService<T> extends BaseService<T> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityDao entityDao;

    public boolean refreshCache(Integer enterpriseId) {
        Set<String> existKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX, getRefreshKeyPrefix(enterpriseId));
        Set<String> dbKeySet = new HashSet<>();
        List<T> list = select(enterpriseId);
        for (T t : list) {
            String key = getKey(t);
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, t);
            dbKeySet.add(key);
        }

        existKeySet.removeAll(dbKeySet);
        if (existKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, existKeySet);
        }

        return true;
    }

    /**
     * 刷新缓存方法
     * @param enterpriseId
     */
    protected void setRefreshCacheMethod(Integer enterpriseId) {
        try {
            Method method = this.getClass().getMethod("refreshCache", Integer.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, enterpriseId);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("AbstractService.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }

    /**
     *
     * @param methodName 必须是public的
     * @param parameterTypes
     * @param parameters
     */
    protected void setCacheMethod(String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        try {
            Method method = this.getClass().getMethod(methodName, parameterTypes);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, parameters);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("AbstractService.setCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName() + " ,method=" + methodName, e);
        }
    }

    /**
     * 根据企业号获取列表
     * @param enterpriseId
     * @return
     */
    protected abstract List<T> select(Integer enterpriseId);

    /**
     * 获取缓存的key
     * @param t
     * @return
     */
    protected abstract String getKey(T t);

    /**
     * 刷新缓存key的前缀
     * @param enterpriseId
     * @return
     */
    protected abstract String getRefreshKeyPrefix(Integer enterpriseId);
}
