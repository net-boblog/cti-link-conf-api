package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.constant.ApiResult;
import com.tinet.ctilink.conf.constant.ApiResultFactory;
import com.tinet.ctilink.conf.dao.EnterpriseSettingDao;
import com.tinet.ctilink.conf.model.EnterpriseSetting;
import com.tinet.ctilink.conf.util.DtoConvertUtil;
import com.tinet.ctilink.conf.dto.EnterpriseSettingDto;
import com.tinet.ctilink.conf.service.EnterpriseSettingService;
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
    public ApiResult<EnterpriseSettingDto> getSettingByName(int enterpriseId, String name) {
        //TODO 参数验证

        EnterpriseSetting enterpriseSetting  = enterpriseSettingDao.selectByName(enterpriseId, name);

        EnterpriseSettingDto enterpriseSettingDto = DtoConvertUtil.assembleEnterpriseSettingToDto(enterpriseSetting);

        return new ApiResult(enterpriseSettingDto);
    }

    @Override
    public ApiResult testSetting() {
        return ApiResultFactory.getSuccessResult();
    }
}
