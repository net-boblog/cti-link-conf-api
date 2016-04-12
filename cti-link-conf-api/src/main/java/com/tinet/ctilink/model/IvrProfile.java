package com.tinet.ctilink.model;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "cti_link_ivr_profile")
public class IvrProfile {
    @Id
    private Integer id;

    private String ivrName;

    private String ivrType;

    private String ivrDescription;

    private Integer enterpriseId;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIvrName() {
        return ivrName;
    }

    public void setIvrName(String ivrName) {
        this.ivrName = ivrName == null ? null : ivrName.trim();
    }

    public String getIvrType() {
        return ivrType;
    }

    public void setIvrType(String ivrType) {
        this.ivrType = ivrType == null ? null : ivrType.trim();
    }

    public String getIvrDescription() {
        return ivrDescription;
    }

    public void setIvrDescription(String ivrDescription) {
        this.ivrDescription = ivrDescription == null ? null : ivrDescription.trim();
    }

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}