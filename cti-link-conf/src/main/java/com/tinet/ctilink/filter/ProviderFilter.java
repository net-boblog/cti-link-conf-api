package com.tinet.ctilink.filter;

import com.alibaba.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fengwei //
 * @date 16/4/9 12:02
 */
public class ProviderFilter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(ProviderFilter.class);

    //一个请求使用一个线程, 缓存刷新方法放到ThreadLocal里面
    public static ThreadLocal<AfterReturningMethod> methodThreadLocal = new ThreadLocal<>();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //IP控制 + 限流

        Result result = null;
        try {
            System.out.println("before returning method invoke...");
            result = invoker.invoke(invocation);
            System.out.println("after returning method invoke...");
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

        return result;
    }
}
