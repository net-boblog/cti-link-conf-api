package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.TelSetTel;

import javax.ws.rs.Path;

/**
 * @author fengwei //
 * @date 16/4/28 09:54
 */
@Path("v1/telSetTel")
public interface TelSetTelService {
    ApiResult createTelSetTel(TelSetTel telSetTel);

    ApiResult deleteTelSetTel(TelSetTel telSetTel);

    ApiResult updateTelSetTel(TelSetTel telSetTel);


    ApiResult getTelSetTels(TelSetTel telSetTel);

}
