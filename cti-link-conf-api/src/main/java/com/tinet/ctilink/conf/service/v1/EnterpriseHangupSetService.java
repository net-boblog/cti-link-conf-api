package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseArea;
import com.tinet.ctilink.conf.model.EnterpriseHangupSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author huangbin
 * @date 2016/5/4.
 */

@Path("v1/enterpriseHangupSet")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnterpriseHangupSetService {
    @POST
    @Path("create")
    ApiResult<EnterpriseHangupSet> createEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet);

    @POST
    @Path("delete")
    ApiResult deleteEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseHangupSet>> listEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet);

}
