package com.tinet.ctilink.request;

/**
 * @author fengwei //
 * @date 16/4/13 18:36
 */
public class EnterpriseMohRequest {
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
