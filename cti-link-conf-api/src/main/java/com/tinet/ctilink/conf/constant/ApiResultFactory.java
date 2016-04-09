package com.tinet.ctilink.conf.constant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author fengwei //
 * @date 16/4/8 17:50
 */
public class ApiResultFactory {

    /**
     * 成功
     */
    public static ApiResult getSuccessResult() {
        return new ApiResult(ApiResult.ResultCode.SUCCESS.getValue(),
                ApiResult.SUCCESS_DESCRIPTION);
    }

    /**
     * 成功, 有数据返回
     */
    public static ApiResult getSuccessResult(Object data) {
        return new ApiResult(ApiResult.ResultCode.SUCCESS.getValue(), ApiResult.SUCCESS_DESCRIPTION, data);
    }

    /**
     * 失败
     */
    public static ApiResult getFailResult(String description) {
        return new ApiResult(ApiResult.ResultCode.FAIL.getValue(), description);
    }

    /**
     * 成功ResponseEntity
     */
    public static ResponseEntity<ApiResult> getSuccessResponse() {
        return new ResponseEntity(getSuccessResult(), HttpStatus.OK);
    }

    /**
     * 失败ResponseEntity
     */
    public static ResponseEntity<ApiResult> getFailResponse(String description) {
        return new ResponseEntity(getFailResult(description), HttpStatus.OK);
    }

    /**
     * 成功ResponseEntity, 有数据返回
     */
    public static ResponseEntity<ApiResult> getResponse(ApiResult apiResult) {
        return new ResponseEntity(apiResult, HttpStatus.OK);
    }
}
