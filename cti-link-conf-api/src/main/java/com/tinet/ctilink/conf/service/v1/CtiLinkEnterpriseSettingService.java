package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseSetting;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:40
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E4%BC%81%E4%B8%9A%E9%85%8D%E7%BD%AE%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/enterpriseSetting")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEnterpriseSettingService {

    @POST
    @Path("create")
    ApiResult<EnterpriseSetting> createEnterpriseSetting(EnterpriseSetting enterpriseSetting);

    @POST
    @Path("update")
    ApiResult updateEnterpriseSetting(EnterpriseSetting enterpriseSetting);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseSetting>> listEnterpriseSetting(EnterpriseSetting enterpriseSetting);

    @POST
    @Path("get")
    ApiResult<EnterpriseSetting> getEnterpriseSetting(EnterpriseSetting enterpriseSetting);

}
