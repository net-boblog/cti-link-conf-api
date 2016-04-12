package com.tinet.ctilink.model;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "cti_link_agent")
public class Agent {
    @Id
    private Integer id;

    private Integer enterpriseId;

    private String cno;

    private String crmId;

    private Integer active;

    private Integer wrapup;

    private String areaCode;

    private String name;

    private Integer callPower;

    private Integer agentType;

    private Integer isOb;

    private Integer ibRecord;

    private Integer obRecord;

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

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno == null ? null : cno.trim();
    }

    public String getCrmId() {
        return crmId;
    }

    public void setCrmId(String crmId) {
        this.crmId = crmId == null ? null : crmId.trim();
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getWrapup() {
        return wrapup;
    }

    public void setWrapup(Integer wrapup) {
        this.wrapup = wrapup;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode == null ? null : areaCode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCallPower() {
        return callPower;
    }

    public void setCallPower(Integer callPower) {
        this.callPower = callPower;
    }

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
    }

    public Integer getIsOb() {
        return isOb;
    }

    public void setIsOb(Integer isOb) {
        this.isOb = isOb;
    }

    public Integer getIbRecord() {
        return ibRecord;
    }

    public void setIbRecord(Integer ibRecord) {
        this.ibRecord = ibRecord;
    }

    public Integer getObRecord() {
        return obRecord;
    }

    public void setObRecord(Integer obRecord) {
        this.obRecord = obRecord;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
