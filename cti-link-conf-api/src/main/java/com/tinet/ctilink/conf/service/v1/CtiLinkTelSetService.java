package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.model.TelSet;
import com.tinet.ctilink.conf.request.CtiLinkTelSetListRequest;

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
    CtiLinkApiResult<TelSet> createTelSet(TelSet telSet);

    @POST
    @Path("delete")
    CtiLinkApiResult deleteTelSet(TelSet telSet);

    @POST
    @Path("update")
    CtiLinkApiResult<TelSet> updateTelSet(TelSet telSet);

    @POST
    @Path("list")
    CtiLinkApiResult listTelSet(CtiLinkTelSetListRequest ctiLinkTelSetListRequest);

    @POST
    @Path("get")
    CtiLinkApiResult getTelSetByIdAndEnterpriseId(TelSet telSet);
}