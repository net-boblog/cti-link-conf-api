package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.request.CtiLinkMohUpdateRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author fengwei //
 * @date 16/4/13 18:32
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E4%BC%81%E4%B8%9A%E8%AF%AD%E9%9F%B3%E5%BA%93%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/enterpriseMoh")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEnterpriseMohService {

    @POST
    @Path("update")
    ApiResult update(CtiLinkMohUpdateRequest ctiLinkMohUpdateRequest);
}
