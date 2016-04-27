package com.tinet.ctilink.conf.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "cti_link_enterprise_voice")
public class EnterpriseVoice {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private Integer enterpriseId;

    private String voiceName;

    private String path;

    private String description;

    private Integer auditStatus;

    private String auditComment;

    private Date createTime;

    private String expiredHour;

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

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName == null ? null : voiceName.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditComment() {
        return auditComment;
    }

    public void setAuditComment(String auditComment) {
        this.auditComment = auditComment == null ? null : auditComment.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getExpiredHour() {
        return expiredHour;
    }

    public void setExpiredHour(String expiredHour) {
        this.expiredHour = expiredHour == null ? null : expiredHour.trim();
    }
}