package com.tinet.ctilink.service;

import com.tinet.ctilink.dto.EnterpriseSettingDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    EnterpriseSettingDto getSettingByName(@QueryParam("enterpriseId") Integer enterpriseId,
                                          @QueryParam("name") String name);

    /**
     * 新增或更新企业配置
     * @param enterpriseId
     * @param name
     * @param value
     * @param property
     * @return
     */
    EnterpriseSettingDto createOrUpdateSetting(Integer enterpriseId, String name, String value, String property);

}
