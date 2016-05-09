package com.tinet.ctilink.conf.request;

import com.tinet.ctilink.conf.model.Agent;

/**
 * @author fengwei //
 * @date 16/4/14 16:34
 */
public class CtiLinkAgentListRequest extends Agent {

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
