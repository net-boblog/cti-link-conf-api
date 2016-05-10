package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.Gateway;
import com.tinet.ctilink.conf.service.v1.CtiLinkGatewayService;
import com.tinet.ctilink.service.BaseService;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/27 19:34
 */
@Service
public class CtiLinkGatewayServiceImp extends BaseService<Gateway> implements CtiLinkGatewayService {

    @Override
    public ApiResult<Gateway> createGateway(Gateway gateway) {
        return null;
    }

    @Override
    public ApiResult deleteGateway(Gateway gateway) {
        return null;
    }

    @Override
    public ApiResult<Gateway> updateGateway(Gateway gateway) {
        return null;
    }

    @Override
    public ApiResult<List<Gateway>> listGateway() {
        return null;
    }
}
