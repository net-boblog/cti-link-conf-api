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
 * @date 16/4/15 16:36
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E5%85%AC%E5%85%B1%E8%AF%AD%E9%9F%B3%E6%96%87%E4%BB%B6%E4%B8%8A%E4%BC%A0%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/publicMoh")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkPublicMohService {

    @POST
    @Path("update")
    ApiResult update(CtiLinkMohUpdateRequest ctiLinkMohUpdateRequest);

}
