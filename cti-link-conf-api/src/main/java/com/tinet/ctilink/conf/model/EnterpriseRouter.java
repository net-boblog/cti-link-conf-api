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

    private Integer obPreviewRouterLeft;

    private Integer obPredictiveRouterLeft;

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

    public Integer getObPreviewRouterLeft() {
        return obPreviewRouterLeft;
    }

    public void setObPreviewRouterLeft(Integer obPreviewRouterLeft) {
        this.obPreviewRouterLeft = obPreviewRouterLeft;
    }

    public Integer getObPredictiveRouterLeft() {
        return obPredictiveRouterLeft;
    }

    public void setObPredictiveRouterLeft(Integer obPredictiveRouterLeft) {
        this.obPredictiveRouterLeft = obPredictiveRouterLeft;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}