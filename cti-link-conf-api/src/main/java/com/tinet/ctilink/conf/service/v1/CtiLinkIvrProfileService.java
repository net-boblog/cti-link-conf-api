package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.model.IvrProfile;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/13 18:21
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E8%AF%AD%E9%9F%B3%E5%AF%BC%E8%88%AA%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/ivrProfile")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkIvrProfileService {
    @POST
    @Path("create")
    CtiLinkApiResult<IvrProfile> createIvrProfile(IvrProfile ivrProfile);

    @POST
    @Path("delete")
    CtiLinkApiResult deleteIvrProfile(IvrProfile ivrProfile);

    @POST
    @Path("update")
    CtiLinkApiResult<IvrProfile> updateIvrProfile(IvrProfile ivrProfile);

    @POST
    @Path("list")
    CtiLinkApiResult<List<IvrProfile>> listIvrProfile(IvrProfile ivrProfile);

    @POST
    @Path("get")
    CtiLinkApiResult<IvrProfile> getIvrProfile(IvrProfile ivrProfile);

}
