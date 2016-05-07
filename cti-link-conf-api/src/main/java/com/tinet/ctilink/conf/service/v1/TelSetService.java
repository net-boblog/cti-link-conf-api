package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.TelSet;
import com.tinet.ctilink.conf.request.TelSetListRequest;

import javax.ws.rs.Path;

/**
 * @author fengwei //
 * @date 16/4/7 17:42
 */
@Path("v1/telSet")
public interface TelSetService {
    ApiResult createTelSet(TelSet telSet);

    ApiResult deleteTelSet(TelSet telSet);

    ApiResult updateTelSet(TelSet telSet);

    ApiResult getListTelSets(TelSetListRequest telSetListRequest);

    ApiResult getTelSetByIdAndEnterpriseId(TelSet telSet);
}
