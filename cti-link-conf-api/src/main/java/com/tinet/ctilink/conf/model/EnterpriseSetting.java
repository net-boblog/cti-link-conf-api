package com.tinet.ctilink.conf.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "cti_link_enterprise_setting")
public class EnterpriseSetting {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private Integer enterpriseId;

    private String name;

    private String value;

    private String property;

    private Date createTime;

    public EnterpriseSetting() {
        this.createTime = new Date();
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property == null ? null : property.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}