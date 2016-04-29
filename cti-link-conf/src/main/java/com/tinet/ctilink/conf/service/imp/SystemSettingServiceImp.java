package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.SystemSetting;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.conf.service.v1.SystemSettingService;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/21 16:36
 */
@Service
public class SystemSettingServiceImp extends BaseService<SystemSetting> implements SystemSettingService {
    @Override
    public ApiResult<SystemSetting> updateSystemSetting(SystemSetting systemSetting) {
        return null;
    }

    @Override
    public ApiResult<List<SystemSetting>> listSystemSetting() {
        return null;
    }
}
