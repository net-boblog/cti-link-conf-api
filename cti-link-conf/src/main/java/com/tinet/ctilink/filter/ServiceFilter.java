package com.tinet.ctilink.filter;

import com.alibaba.dubbo.rpc.*;
import com.tinet.ctilink.ApiResult;

import java.lang.reflect.Method;

/**
 * @author fengwei //
 * @date 16/4/9 12:02
 */
public class ServiceFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // before filter ...
        System.out.println("before dubbo filter");
        Result result = invoker.invoke(invocation);

        if (result.getValue() instanceof ApiResult) {
            System.out.println("ApiResult result");
            int resultCode = ((ApiResult) result.getValue()).getResult();
            if (resultCode == ApiResult.SUCCESS_RESULT) {
                System.out.println("Success");
            }
        }
        if (invocation.getMethodName().startsWith("create")
                || invocation.getMethodName().startsWith("update")
                || invocation.getMethodName().startsWith("delete")) {
            System.out.println("need refresh cache");
        }

        Class clazz = invoker.getInterface();

        System.out.println("after dubbo filter");
        // after filter ...
        return result;
    }
}
