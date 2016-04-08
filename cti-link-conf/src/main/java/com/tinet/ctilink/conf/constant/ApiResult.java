package com.tinet.ctilink.conf.constant;

import java.io.Serializable;

/**
 * @author fengwei //
 * @date 16/4/8 17:51
 */
public class ApiResult implements Serializable {

    private int result;

    private String description;

    private Object data;

    public ApiResult(int result, String description) {
        this.result = result;
        this.description = description;
    }

    public ApiResult(int result, String description, Object data) {
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
