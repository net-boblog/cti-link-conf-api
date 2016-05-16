package com.tinet.ctilink.conf.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "cti_link_enterprise_clid")
public class EnterpriseClid implements Serializable {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private Integer enterpriseId;

    private Integer ibClidRightType;

    private String ibClidRightNumber;

    private Integer obClidLeftType;

    private String obClidLeftNumber;

    private Integer obClidRightType;

    private String obClidRightNumber;

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

    public Integer getIbClidRightType() {
        return ibClidRightType;
    }

    public void setIbClidRightType(Integer ibClidRightType) {
        this.ibClidRightType = ibClidRightType;
    }

    public String getIbClidRightNumber() {
        return ibClidRightNumber;
    }

    public void setIbClidRightNumber(String ibClidRightNumber) {
        this.ibClidRightNumber = ibClidRightNumber == null ? null : ibClidRightNumber.trim();
    }

    public Integer getObClidLeftType() {
        return obClidLeftType;
    }

    public void setObClidLeftType(Integer obClidLeftType) {
        this.obClidLeftType = obClidLeftType;
    }

    public String getObClidLeftNumber() {
        return obClidLeftNumber;
    }

    public void setObClidLeftNumber(String obClidLeftNumber) {
        this.obClidLeftNumber = obClidLeftNumber == null ? null : obClidLeftNumber.trim();
    }

    public Integer getObClidRightType() {
        return obClidRightType;
    }

    public void setObClidRightType(Integer obClidRightType) {
        this.obClidRightType = obClidRightType;
    }

    public String getObClidRightNumber() {
        return obClidRightNumber;
    }

    public void setObClidRightNumber(String obClidRightNumber) {
        this.obClidRightNumber = obClidRightNumber == null ? null : obClidRightNumber.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}