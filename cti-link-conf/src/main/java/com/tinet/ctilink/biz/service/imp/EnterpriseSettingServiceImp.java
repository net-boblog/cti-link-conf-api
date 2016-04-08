package com.tinet.ctilink.biz.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.biz.dao.EnterpriseSettingDao;
import com.tinet.ctilink.biz.model.EnterpriseSetting;
import com.tinet.ctilink.biz.util.DtoConvertUtil;
import com.tinet.ctilink.dto.EnterpriseSettingDto;
import com.tinet.ctilink.service.EnterpriseSettingService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fengwei //
 * @date 16/4/7 17:20
 */
@Service
public class EnterpriseSettingServiceImp implements EnterpriseSettingService {

    @Autowired
    EnterpriseSettingDao enterpriseSettingDao;

    @Override
    public EnterpriseSettingDto getSettingByName(Integer enterpriseId, String name) {
        EnterpriseSetting enterpriseSetting  = enterpriseSettingDao.selectByName(enterpriseId, name);

        return DtoConvertUtil.assembleEnterpriseSettingToDto(enterpriseSetting);
    }

    @Override
    public EnterpriseSettingDto createOrUpdateSetting(Integer enterpriseId, String name, String value, String property) {

        return null;
    }
}
