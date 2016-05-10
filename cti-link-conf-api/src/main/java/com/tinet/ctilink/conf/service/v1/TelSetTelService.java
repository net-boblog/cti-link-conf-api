package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.TelSetTel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author huangbin //
 * @date 16/4/28 09:54
 */
@Path("v1/telSetTel")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface TelSetTelService {
    @POST
    @Path("create")
    ApiResult createTelSetTel(TelSetTel telSetTel);

    @POST
    @Path("delete")
    ApiResult deleteTelSetTel(TelSetTel telSetTel);

    @POST
    @Path("update")
    ApiResult updateTelSetTel(TelSetTel telSetTel);

    @POST
    @Path("list")
    ApiResult listTelSetTel(TelSetTel telSetTel);

}
