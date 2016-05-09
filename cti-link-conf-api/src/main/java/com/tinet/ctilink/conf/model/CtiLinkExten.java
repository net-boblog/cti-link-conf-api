package com.tinet.ctilink.conf.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author fengwei //
 * @date 16/5/9 10:21
 */
@Table(name = "cti_link_exten")
public class CtiLinkExten implements Serializable {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private Integer enterpriseId;

    private String exten;

    private Integer callPower;

    private Integer isOb;

    private Integer ibRecord;

    private Integer obRecord;

    private String areaCode;

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

    public String getExten() {
        return exten;
    }

    public void setExten(String exten) {
        this.exten = exten == null ? null : exten.trim();
    }

    public Integer getCallPower() {
        return callPower;
    }

    public void setCallPower(Integer callPower) {
        this.callPower = callPower;
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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode == null ? null : areaCode.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
