package com.tinet.ctilink.conf.request;

import com.tinet.ctilink.conf.model.RestrictTel;

/**
 * Created by nope-J on 2016/5/3.
 */
public class RestrictTelRequest extends RestrictTel {
    int limit;
    int offset;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
