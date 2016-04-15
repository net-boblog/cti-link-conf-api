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

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //IP控制

        //限流

        Result result = invoker.invoke(invocation);
        return result;
    }
}
