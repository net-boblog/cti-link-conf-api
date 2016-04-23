package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseInvestigation;

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
public interface EnterpriseInvestigationService {

    @POST
    @Path("create")
    ApiResult<EnterpriseInvestigation> create(EnterpriseInvestigation enterpriseInvestigation);

    @POST
    @Path("delete")
    ApiResult<EnterpriseInvestigation> delete(EnterpriseInvestigation enterpriseInvestigation);

    @POST
    @Path("update")
    ApiResult<EnterpriseInvestigation> update(EnterpriseInvestigation enterpriseInvestigation);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseInvestigation>> list(EnterpriseInvestigation enterpriseInvestigation);

    @POST
    @Path("get")
    ApiResult<EnterpriseInvestigation> get(EnterpriseInvestigation enterpriseInvestigation);

}
