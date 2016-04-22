package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseArea;

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
@Path("v1/enterpriseArea")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnterpriseAreaService {

    @POST
    @Path("create")
    ApiResult<EnterpriseArea> create(EnterpriseArea enterpriseArea);

    @POST
    @Path("delete")
    ApiResult delete(EnterpriseArea enterpriseArea);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseArea>> list(EnterpriseArea enterpriseArea);

}
