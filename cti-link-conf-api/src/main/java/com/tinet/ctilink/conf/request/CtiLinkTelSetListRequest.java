package com.tinet.ctilink.conf.request;

import com.tinet.ctilink.conf.model.TelSet;

/**
 * @author fengwei //
 * @date 16/4/14 16:34
 */
public class CtiLinkTelSetListRequest extends TelSet {

    private int limit;

    private int offset;


    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
