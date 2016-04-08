package com.tinet.ctilink.biz.util;

import com.tinet.ctilink.biz.model.EnterpriseSetting;
import com.tinet.ctilink.dto.EnterpriseSettingDto;

/**
 * @author fengwei //
 * @date 16/4/8 14:59
 */
public class DtoConvertUtil {

    public static EnterpriseSettingDto assembleEnterpriseSettingToDto(EnterpriseSetting enterpriseSetting) {
        if (enterpriseSetting == null) {
            return null;
        }
        EnterpriseSettingDto dto = new EnterpriseSettingDto();
        dto.setEnterpriseId(enterpriseSetting.getEnterpriseId());
        dto.setName(enterpriseSetting.getName());
        dto.setValue(enterpriseSetting.getValue());
        dto.setProperty(enterpriseSetting.getProperty());
        return dto;
    }
}
