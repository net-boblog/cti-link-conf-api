package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.TelSet;
import com.tinet.ctilink.conf.request.TelSetListRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author huangbin //
 * @date 16/4/7 17:42
 */
@Path("v1/telSet")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkTelSetService {
    @POST
    @Path("create")
    ApiResult<TelSet> createTelSet(TelSet telSet);

    @POST
    @Path("delete")
    ApiResult deleteTelSet(TelSet telSet);

    @POST
    @Path("update")
    ApiResult<TelSet> updateTelSet(TelSet telSet);

    @POST
    @Path("list")
    ApiResult listTelSet(TelSetListRequest telSetListRequest);

    @POST
    @Path("get")
    ApiResult getTelSetByIdAndEnterpriseId(TelSet telSet);
}