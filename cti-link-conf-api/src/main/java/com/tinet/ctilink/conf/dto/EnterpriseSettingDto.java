package com.tinet.ctilink.conf.dto;

import java.util.Date;

/**
 * @author fengwei //
 * @date 16/4/8 13:29
 */
public class EnterpriseSettingDto {
    // Fields
    private Integer enterpriseId;
    private String name;
    private String value;
    private String property;

    // Constructors
    /** default constructor */
    public EnterpriseSettingDto(){
    }

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

}
