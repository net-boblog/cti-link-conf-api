package com.tinet.ctilink.conf.filter;

import com.alibaba.dubbo.rpc.*;

/**
 * @author fengwei //
 * @date 16/4/9 12:02
 */
public class DubboFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // before filter ...
        System.out.println("before dubbo filter");
        Result result = invoker.invoke(invocation);
        System.out.println("after dubbo filter");
        // after filter ...
        return result;
    }
}
