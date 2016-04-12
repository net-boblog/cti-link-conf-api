package com.tinet.ctilink.model;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "cti_link_agent_crontab")
public class AgentCrontab {
    @Id
    private Long id;

    private Integer enterpriseId;

    private Integer agentId;

    private Integer cronType;

    private String dayOfWeek;

    private String startTime;

    private Integer agentTelId;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public Integer getCronType() {
        return cronType;
    }

    public void setCronType(Integer cronType) {
        this.cronType = cronType;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek == null ? null : dayOfWeek.trim();
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime == null ? null : startTime.trim();
    }

    public Integer getAgentTelId() {
        return agentTelId;
    }

    public void setAgentTelId(Integer agentTelId) {
        this.agentTelId = agentTelId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}