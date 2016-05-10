package com.tinet.ctilink.conf.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "cti_link_queue")
public class Queue {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private Integer enterpriseId;

    private String qno;

    private String description;

    private String musicClass;

    private Integer queueTimeout;

    private Boolean sayAgentno;

    private Integer memberTimeout;

    private Integer retry;

    private Integer wrapupTime;

    private Integer maxLen;

    private String strategy;

    private Integer serviceLevel;

    private Integer weight;

    private Integer vipSupport;

    private Integer joinEmpty;

    private Integer announceSound;

    private Integer announceSoundFrequency;

    private String announceSoundFile;

    private Integer announcePosition;

    private Integer announcePositionYouarenext;

    private Integer announcePositionFrequency;

    private Integer announcePositionParam;

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

    public String getQno() {
        return qno;
    }

    public void setQno(String qno) {
        this.qno = qno == null ? null : qno.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getMusicClass() {
        return musicClass;
    }

    public void setMusicClass(String musicClass) {
        this.musicClass = musicClass == null ? null : musicClass.trim();
    }

    public Integer getQueueTimeout() {
        return queueTimeout;
    }

    public void setQueueTimeout(Integer queueTimeout) {
        this.queueTimeout = queueTimeout;
    }

    public Boolean getSayAgentno() {
        return sayAgentno;
    }

    public void setSayAgentno(Boolean sayAgentno) {
        this.sayAgentno = sayAgentno;
    }

    public Integer getMemberTimeout() {
        return memberTimeout;
    }

    public void setMemberTimeout(Integer memberTimeout) {
        this.memberTimeout = memberTimeout;
    }

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public Integer getWrapupTime() {
        return wrapupTime;
    }

    public void setWrapupTime(Integer wrapupTime) {
        this.wrapupTime = wrapupTime;
    }

    public Integer getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(Integer maxLen) {
        this.maxLen = maxLen;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy == null ? null : strategy.trim();
    }

    public Integer getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(Integer serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getVipSupport() {
        return vipSupport;
    }

    public void setVipSupport(Integer vipSupport) {
        this.vipSupport = vipSupport;
    }

    public Integer getJoinEmpty() {
        return joinEmpty;
    }

    public void setJoinEmpty(Integer joinEmpty) {
        this.joinEmpty = joinEmpty;
    }

    public Integer getAnnounceSound() {
        return announceSound;
    }

    public void setAnnounceSound(Integer announceSound) {
        this.announceSound = announceSound;
    }

    public Integer getAnnounceSoundFrequency() {
        return announceSoundFrequency;
    }

    public void setAnnounceSoundFrequency(Integer announceSoundFrequency) {
        this.announceSoundFrequency = announceSoundFrequency;
    }

    public String getAnnounceSoundFile() {
        return announceSoundFile;
    }

    public void setAnnounceSoundFile(String announceSoundFile) {
        this.announceSoundFile = announceSoundFile == null ? null : announceSoundFile.trim();
    }

    public Integer getAnnouncePosition() {
        return announcePosition;
    }

    public void setAnnouncePosition(Integer announcePosition) {
        this.announcePosition = announcePosition;
    }

    public Integer getAnnouncePositionYouarenext() {
        return announcePositionYouarenext;
    }

    public void setAnnouncePositionYouarenext(Integer announcePositionYouarenext) {
        this.announcePositionYouarenext = announcePositionYouarenext;
    }

    public Integer getAnnouncePositionFrequency() {
        return announcePositionFrequency;
    }

    public void setAnnouncePositionFrequency(Integer announcePositionFrequency) {
        this.announcePositionFrequency = announcePositionFrequency;
    }

    public Integer getAnnouncePositionParam() {
        return announcePositionParam;
    }

    public void setAnnouncePositionParam(Integer announcePositionParam) {
        this.announcePositionParam = announcePositionParam;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
