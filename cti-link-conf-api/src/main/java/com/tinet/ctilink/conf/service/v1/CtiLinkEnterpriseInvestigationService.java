package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseInvestigation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/14 11:27
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E6%BB%A1%E6%84%8F%E5%BA%A6%E8%B0%83%E6%9F%A5%E8%AE%BE%E7%BD%AE%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/enterpriseInvestigation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEnterpriseInvestigationService {

    @POST
    @Path("create")
    ApiResult<EnterpriseInvestigation> createEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation);

    @POST
    @Path("delete")
    ApiResult deleteEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation);

    @POST
    @Path("update")
    ApiResult<EnterpriseInvestigation> updateEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseInvestigation>> listEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation);

    @POST
    @Path("get")
    ApiResult<EnterpriseInvestigation> getEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation);

}
