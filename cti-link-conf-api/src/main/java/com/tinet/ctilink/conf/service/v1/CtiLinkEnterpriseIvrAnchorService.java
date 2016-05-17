package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.CtiLinkEnterpriseIvrAnchor;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/17 09:14
 */
@Path("v1/enterpriseIvrAnchor")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEnterpriseIvrAnchorService {
    @POST
    @Path("create")
    ApiResult<CtiLinkEnterpriseIvrAnchor> createEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor);

    @POST
    @Path("delete")
    ApiResult deleteEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor);

    @POST
    @Path("update")
    ApiResult<CtiLinkEnterpriseIvrAnchor> updateEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor);

    @POST
    @Path("list")
    ApiResult<List<CtiLinkEnterpriseIvrAnchor>> listEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor);

    @POST
    @Path("get")
    ApiResult<CtiLinkEnterpriseIvrAnchor> getEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor);
}
