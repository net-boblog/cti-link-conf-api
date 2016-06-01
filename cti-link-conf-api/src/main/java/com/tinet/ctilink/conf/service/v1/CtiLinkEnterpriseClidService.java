package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseClid;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author fengwei //
 * @date 16/5/31 14:33
 */
@Path("v1/enterpriseClid")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEnterpriseClidService {

    @POST
    @Path("update")
    ApiResult<EnterpriseClid> updateEnterpriseClid(EnterpriseClid enterpriseClid);

}
