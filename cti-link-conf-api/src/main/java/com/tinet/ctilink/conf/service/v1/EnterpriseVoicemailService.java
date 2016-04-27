package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseVoicemail;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/14 11:27
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E7%95%99%E8%A8%80%E7%AE%B1%E8%AE%BE%E7%BD%AE%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/enterpriseVoicemail")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnterpriseVoicemailService {

    @POST
    @Path("create")
    ApiResult<EnterpriseVoicemail> create(EnterpriseVoicemail enterpriseVoicemail);

    @POST
    @Path("delete")
    ApiResult delete(EnterpriseVoicemail enterpriseVoicemail);

    @POST
    @Path("update")
    ApiResult<EnterpriseVoicemail> update(EnterpriseVoicemail enterpriseVoicemail);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseVoicemail>> list(EnterpriseVoicemail enterpriseVoicemail);

}
