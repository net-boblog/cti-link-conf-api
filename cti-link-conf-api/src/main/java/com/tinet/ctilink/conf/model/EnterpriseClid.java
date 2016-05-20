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

    private Integer obPreviewClidLeftType;

    private String obPreviewClidLeftNumber;

    private Integer obPreviewClidRightType;

    private String obPreviewClidRightNumber;

    private Integer obPredictiveClidLeftType;

    private String obPredictiveClidLeftNumber;

    private Integer obPredictiveClidRightType;

    private String obPredictiveClidRightNumber;

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

    public Integer getObPreviewClidLeftType() {
        return obPreviewClidLeftType;
    }

    public void setObPreviewClidLeftType(Integer obPreviewClidLeftType) {
        this.obPreviewClidLeftType = obPreviewClidLeftType;
    }

    public String getObPreviewClidLeftNumber() {
        return obPreviewClidLeftNumber;
    }

    public void setObPreviewClidLeftNumber(String obPreviewClidLeftNumber) {
        this.obPreviewClidLeftNumber = obPreviewClidLeftNumber == null ? null : obPreviewClidLeftNumber.trim();
    }

    public Integer getObPreviewClidRightType() {
        return obPreviewClidRightType;
    }

    public void setObPreviewClidRightType(Integer obPreviewClidRightType) {
        this.obPreviewClidRightType = obPreviewClidRightType;
    }

    public String getObPreviewClidRightNumber() {
        return obPreviewClidRightNumber;
    }

    public void setObPreviewClidRightNumber(String obPreviewClidRightNumber) {
        this.obPreviewClidRightNumber = obPreviewClidRightNumber == null ? null : obPreviewClidRightNumber.trim();
    }

    public Integer getObPredictiveClidLeftType() {
        return obPredictiveClidLeftType;
    }

    public void setObPredictiveClidLeftType(Integer obPredictiveClidLeftType) {
        this.obPredictiveClidLeftType = obPredictiveClidLeftType;
    }

    public String getObPredictiveClidLeftNumber() {
        return obPredictiveClidLeftNumber;
    }

    public void setObPredictiveClidLeftNumber(String obPredictiveClidLeftNumber) {
        this.obPredictiveClidLeftNumber = obPredictiveClidLeftNumber == null ? null : obPredictiveClidLeftNumber.trim();
    }

    public Integer getObPredictiveClidRightType() {
        return obPredictiveClidRightType;
    }

    public void setObPredictiveClidRightType(Integer obPredictiveClidRightType) {
        this.obPredictiveClidRightType = obPredictiveClidRightType;
    }

    public String getObPredictiveClidRightNumber() {
        return obPredictiveClidRightNumber;
    }

    public void setObPredictiveClidRightNumber(String obPredictiveClidRightNumber) {
        this.obPredictiveClidRightNumber = obPredictiveClidRightNumber == null ? null : obPredictiveClidRightNumber.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}