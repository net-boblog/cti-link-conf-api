package com.tinet.ctilink.conf.constant;

/**
 * @author fengwei //
 * @date 16/4/8 17:50
 */
public class ApiResultFactory {

    /**
     * 成功
     */
    public static ApiResult getRequestSuccess() {
        return new ApiResult(ApiResultCode.SUCCESS, "成功");
    }

    /**
     * 成功, 有数据返回
     */
    public static ApiResult getRequestSuccess(Object data) {
        return new ApiResult(ApiResultCode.SUCCESS, "成功", data);
    }

    /**
     * 失败
     */
    public static ApiResult getRequestFail(String description) {
        return new ApiResult(ApiResultCode.FAIL, description);
    }
}
