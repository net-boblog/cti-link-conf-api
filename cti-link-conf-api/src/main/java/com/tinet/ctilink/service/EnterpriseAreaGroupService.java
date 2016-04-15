package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseAreaGroup;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/15 16:41
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E5%9C%B0%E5%8C%BA%E7%BB%84%E8%AE%BE%E7%BD%AE%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/enterpriseAreaGroup")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnterpriseAreaGroupService {

    @POST
    @Path("create")
    ApiResult<EnterpriseAreaGroup> create(EnterpriseAreaGroup enterpriseAreaGroup);

    @POST
    @Path("delete")
    ApiResult delete(EnterpriseAreaGroup enterpriseAreaGroup);

    @POST
    @Path("update")
    ApiResult<EnterpriseAreaGroup> update(EnterpriseAreaGroup enterpriseAreaGroup);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseAreaGroup>> list(EnterpriseAreaGroup enterpriseAreaGroup);

}
