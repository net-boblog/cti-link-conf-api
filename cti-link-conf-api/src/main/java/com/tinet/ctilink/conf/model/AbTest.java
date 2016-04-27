package com.tinet.ctilink.conf.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author fengwei //
 * @date 16/4/14 14:59
 */
@Table(name = "cti_link_ab_test")
public class AbTest {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private Integer enterpriseId;

    private Integer sipGroupId;

    private String comment;

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

    public Integer getSipGroupId() {
        return sipGroupId;
    }

    public void setSipGroupId(Integer sipGroupId) {
        this.sipGroupId = sipGroupId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
