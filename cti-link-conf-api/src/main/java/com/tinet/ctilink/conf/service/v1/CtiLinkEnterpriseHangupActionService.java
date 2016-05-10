package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseHangupAction;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author huangbin
 * @date 2016/4/29.
 */

@Path("v1/enterpriseHangupAction")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEnterpriseHangupActionService {
    @POST
    @Path("create")
    ApiResult<EnterpriseHangupAction> createEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction);

    @POST
    @Path("delete")
    ApiResult deleteEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction);

    @POST
    @Path("update")
    ApiResult<EnterpriseHangupAction> updateEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseHangupAction>> listEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction);

    @POST
    @Path("get")
    ApiResult<EnterpriseHangupAction> getEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction);

}
