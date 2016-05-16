package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.model.EnterpriseIvrRouter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author huangbin //
 * @date 16/4/15 16:38
 */
@Path("v1/enterpriseIvrRouter")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEnterpriseIvrRouterService {

    @POST
    @Path("create")
    CtiLinkApiResult<EnterpriseIvrRouter> createEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter);

    @POST
    @Path("delete")
    CtiLinkApiResult deleteEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter);

    @POST
    @Path("update")
    CtiLinkApiResult<EnterpriseIvrRouter> updateEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter);

    @POST
    @Path("list")
    CtiLinkApiResult<List<EnterpriseIvrRouter>> listEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter);

    @POST
    @Path("get")
    CtiLinkApiResult<EnterpriseIvrRouter> getEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter);

}
