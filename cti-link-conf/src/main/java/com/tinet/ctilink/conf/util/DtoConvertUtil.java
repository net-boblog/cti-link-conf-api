package com.tinet.ctilink.conf.util;

import com.tinet.ctilink.conf.model.EnterpriseSetting;
import com.tinet.ctilink.conf.dto.EnterpriseSettingDto;

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
