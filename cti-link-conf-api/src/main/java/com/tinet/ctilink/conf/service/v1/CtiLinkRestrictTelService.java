package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.model.RestrictTel;
import com.tinet.ctilink.conf.request.CtiLinkRestrictTelRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author huangbin //
 * @date 16/4/14 11:27
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E9%BB%91%E7%99%BD%E5%90%8D%E5%8D%95%E6%8E%A5%E5%8F%A3-v1
 */

@Path("v1/restrictTel")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkRestrictTelService {

    @POST
    @Path("create")
    CtiLinkApiResult<RestrictTel> createRestrictTel(RestrictTel restrictTel);

    @POST
    @Path("delete")
    CtiLinkApiResult deleteRestrictTel(RestrictTel restrictTel);

    @POST
    @Path("list")
    CtiLinkApiResult listRestrictTel(CtiLinkRestrictTelRequest ctiLinkRestrictTelRequest);
}
