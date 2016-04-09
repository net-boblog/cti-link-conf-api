package com.tinet.ctilink.conf.constant;

import java.io.Serializable;

/**
 * @author fengwei //
 * @date 16/4/8 17:51
 */
public class ApiResult<T> implements Serializable {
    // 成功描述
    public static final String SUCCESS_DESCRIPTION = "操作成功";

    private int result;  //0成功, -1失败  see ResultCode

    private String description;

    private T data;

    public enum ResultCode {
        SUCCESS(0), FAIL(-1);
        private int value;

        ResultCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public ApiResult(T data) {
        this.result = ResultCode.SUCCESS.getValue();
        this.description = SUCCESS_DESCRIPTION;
        this.data = data;
    }

    public ApiResult(int result, String description) {
        this.result = result;
        this.description = description;
    }

    public ApiResult(int result, String description, T data) {
        this.result = result;
        this.description = description;
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        //TODO JSON
        return super.toString();
    }
}
