package com.tinet.ctilink.biz.model;

import java.util.Date;

public class EnterpriseMohVoice {
    private Integer id;

    private Integer enterpriseId;

    private Integer mohId;

    private Integer voiceId;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Integer getMohId() {
        return mohId;
    }

    public void setMohId(Integer mohId) {
        this.mohId = mohId;
    }

    public Integer getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(Integer voiceId) {
        this.voiceId = voiceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}