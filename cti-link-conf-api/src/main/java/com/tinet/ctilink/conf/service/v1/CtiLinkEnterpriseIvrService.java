package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.model.EnterpriseIvr;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/22 13:44
 */
@Path("v1/enterpriseIvr")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEnterpriseIvrService {
    @POST
    @Path("create")
    CtiLinkApiResult<EnterpriseIvr> createEnterpriseIvr(EnterpriseIvr enterpriseIvr);

    @POST
    @Path("delete")
    CtiLinkApiResult deleteEnterpriseIvr(EnterpriseIvr enterpriseIvr);

    @POST
    @Path("update")
    CtiLinkApiResult<EnterpriseIvr> updateEnterpriseIvr(EnterpriseIvr enterpriseIvr);

    @POST
    @Path("list")
    CtiLinkApiResult<List<EnterpriseIvr>> listEnterpriseIvr(EnterpriseIvr enterpriseIvr);

    @POST
    @Path("get")
    CtiLinkApiResult<EnterpriseIvr> getEnterpriseIvr(EnterpriseIvr enterpriseIvr);
}
