package com.tinet.ctilink.conf.model;

import java.util.Date;

public class EnterpriseIvrRouter {
    private Integer id;

    private Integer enterpriseId;

    private Integer active;

    private Integer routerType;

    private String routerProperty;

    private String description;

    private Integer priority;

    private String ruleTimeProperty;

    private String ruleAreaProperty;

    private String ruleTrunkProperty;

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

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getRouterType() {
        return routerType;
    }

    public void setRouterType(Integer routerType) {
        this.routerType = routerType;
    }

    public String getRouterProperty() {
        return routerProperty;
    }

    public void setRouterProperty(String routerProperty) {
        this.routerProperty = routerProperty == null ? null : routerProperty.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getRuleTimeProperty() {
        return ruleTimeProperty;
    }

    public void setRuleTimeProperty(String ruleTimeProperty) {
        this.ruleTimeProperty = ruleTimeProperty == null ? null : ruleTimeProperty.trim();
    }

    public String getRuleAreaProperty() {
        return ruleAreaProperty;
    }

    public void setRuleAreaProperty(String ruleAreaProperty) {
        this.ruleAreaProperty = ruleAreaProperty == null ? null : ruleAreaProperty.trim();
    }

    public String getRuleTrunkProperty() {
        return ruleTrunkProperty;
    }

    public void setRuleTrunkProperty(String ruleTrunkProperty) {
        this.ruleTrunkProperty = ruleTrunkProperty == null ? null : ruleTrunkProperty.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}