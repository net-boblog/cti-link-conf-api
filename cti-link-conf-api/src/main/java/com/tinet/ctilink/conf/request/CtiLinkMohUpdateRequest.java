package com.tinet.ctilink.conf.request;

/**
 * @author fengwei //
 * @date 16/4/13 18:36
 *
 * 企业和公共语音库设置等待语音请求
 */
public class CtiLinkMohUpdateRequest {
    public int enterpriseId;

    public int voiceId;

    public int isMoh;

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public int getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(int voiceId) {
        this.voiceId = voiceId;
    }

    public int getIsMoh() {
        return isMoh;
    }

    public void setIsMoh(int isMoh) {
        this.isMoh = isMoh;
    }
}
