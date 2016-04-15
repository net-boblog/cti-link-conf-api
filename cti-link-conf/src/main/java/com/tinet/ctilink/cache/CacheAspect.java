package com.tinet.ctilink.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fengwei //
 * @date 16/4/15 15:34
 */
public class CacheAspect {
    private final static Logger logger = LoggerFactory.getLogger(CacheAspect.class);

    //一个请求使用一个线程, 缓存刷新方法放到ThreadLocal里面
    public static ThreadLocal<AfterReturningMethod> methodThreadLocal = new ThreadLocal<>();

    public void afterReturning() throws Throwable {
        try {
            try {
                AfterReturningMethod method = methodThreadLocal.get();
                if (method != null) {
                    method.invoke();
                }
            } catch (Exception e) {
                logger.error("AfterReturningMethod invoke error, ", e);
            }

        } finally {
            //线程可能重用, 每次调用必须remove
            methodThreadLocal.remove();
        }
    }
}
