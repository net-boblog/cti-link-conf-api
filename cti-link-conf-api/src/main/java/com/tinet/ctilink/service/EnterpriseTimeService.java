package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseTime;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/15 16:56
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E6%97%B6%E9%97%B4%E6%9D%A1%E4%BB%B6%E8%AE%BE%E7%BD%AE%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/enterpriseTime")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnterpriseTimeService {

    @POST
    @Path("create")
    ApiResult<EnterpriseTime> create(EnterpriseTime enterpriseTime);

    @POST
    @Path("delete")
    ApiResult delete(EnterpriseTime enterpriseTime);

    @POST
    @Path("update")
    ApiResult<EnterpriseTime> update(EnterpriseTime enterpriseTime);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseTime>> list(EnterpriseTime enterpriseTime);

}
