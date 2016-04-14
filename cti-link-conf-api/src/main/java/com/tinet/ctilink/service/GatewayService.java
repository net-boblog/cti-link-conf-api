package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.Gateway;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/14 14:27
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E7%BD%91%E5%85%B3%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/gateway")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface GatewayService {

    @POST
    @Path("create")
    ApiResult<Gateway> create(Gateway gateway);

    @POST
    @Path("delete")
    ApiResult delete(Gateway gateway);

    @POST
    @Path("update")
    ApiResult<Gateway> update(Gateway gateway);

    @POST
    @Path("list")
    ApiResult<List<Gateway>> list();

}
