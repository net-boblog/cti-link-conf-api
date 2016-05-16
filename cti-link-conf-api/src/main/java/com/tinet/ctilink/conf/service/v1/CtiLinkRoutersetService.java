package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.model.Routerset;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/14 14:32
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E8%B7%AF%E7%94%B1%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/routerset")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkRoutersetService {

    @POST
    @Path("create")
    CtiLinkApiResult<Routerset> create(Routerset routerset);

    @POST
    @Path("delete")
    CtiLinkApiResult delete(Routerset routerset);

    @POST
    @Path("update")
    CtiLinkApiResult<Routerset> update(Routerset routerset);

    @POST
    @Path("list")
    CtiLinkApiResult<List<Routerset>> list();

}
