package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.Gateway;

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
public interface CtiLinkGatewayService {

    @POST
    @Path("create")
    ApiResult<Gateway> createGateway(Gateway gateway);

    @POST
    @Path("delete")
    ApiResult deleteGateway(Gateway gateway);

    @POST
    @Path("update")
    ApiResult<Gateway> updateGateway(Gateway gateway);

    @POST
    @Path("list")
    ApiResult<List<Gateway>> listGateway();

}
