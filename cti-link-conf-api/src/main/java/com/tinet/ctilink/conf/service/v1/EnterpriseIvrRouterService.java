package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseIvrRouter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/15 16:38
 */
@Path("v1/enterpriseIvrRouter")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnterpriseIvrRouterService {

    @POST
    @Path("create")
    ApiResult<EnterpriseIvrRouter> create(EnterpriseIvrRouter enterpriseIvrRouter);

    @POST
    @Path("delete")
    ApiResult delete(EnterpriseIvrRouter enterpriseIvrRouter);

    @POST
    @Path("update")
    ApiResult<EnterpriseIvrRouter> update(EnterpriseIvrRouter enterpriseIvrRouter);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseIvrRouter>> list(EnterpriseIvrRouter enterpriseIvrRouter);

    @POST
    @Path("get")
    ApiResult<EnterpriseIvrRouter> get(EnterpriseIvrRouter enterpriseIvrRouter);

}
