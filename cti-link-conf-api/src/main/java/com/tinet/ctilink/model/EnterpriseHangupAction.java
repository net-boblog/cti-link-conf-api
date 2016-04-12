package com.tinet.ctilink.model;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "cti_link_enterprise_hangup_action")
public class EnterpriseHangupAction {
    @Id
    private Integer id;

    private Integer enterpriseId;

    private String url;

    private String paramName;

    private String paramVariable;

    private Integer timeout;

    private Integer retry;

    private Date createTime;

    private Integer type;

    private Integer intervalTime;

    private Integer method;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName == null ? null : paramName.trim();
    }

    public String getParamVariable() {
        return paramVariable;
    }

    public void setParamVariable(String paramVariable) {
        this.paramVariable = paramVariable == null ? null : paramVariable.trim();
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Integer intervalTime) {
        this.intervalTime = intervalTime;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }
}