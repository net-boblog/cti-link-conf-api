package com.tinet.ctilink.conf.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "cti_link_enterprise_router")
public class EnterpriseRouter implements Serializable {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private Integer enterpriseId;

    private Integer ibRouterRight;

    private Integer obRouterLeft;

    private Integer obRouterRight;

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

    public Integer getIbRouterRight() {
        return ibRouterRight;
    }

    public void setIbRouterRight(Integer ibRouterRight) {
        this.ibRouterRight = ibRouterRight;
    }

    public Integer getObRouterLeft() {
        return obRouterLeft;
    }

    public void setObRouterLeft(Integer obRouterLeft) {
        this.obRouterLeft = obRouterLeft;
    }

    public Integer getObRouterRight() {
        return obRouterRight;
    }

    public void setObRouterRight(Integer obRouterRight) {
        this.obRouterRight = obRouterRight;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}