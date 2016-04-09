package com.tinet.ctilink.conf.service;

import com.tinet.ctilink.conf.constant.ApiResult;
import com.tinet.ctilink.conf.dto.EnterpriseSettingDto;

/**
 * @author fengwei //
 * @date 16/4/7 17:40
 */
public interface EnterpriseSettingService {

    /**
     * 根据名字获取企业配置
     * @param enterpriseId
     * @param name
     * @return
     */
    ApiResult<EnterpriseSettingDto> getSettingByName(int enterpriseId, String name);

    ApiResult testSetting();
}
