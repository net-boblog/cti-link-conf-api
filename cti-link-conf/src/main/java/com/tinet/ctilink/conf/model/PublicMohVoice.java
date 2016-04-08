package com.tinet.ctilink.conf.model;

import java.util.Date;

public class PublicMohVoice {
    private Integer id;

    private Integer mohId;

    private Integer voiceId;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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