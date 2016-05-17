package com.tinet.ctilink.conf.service.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * @author fengwei //
 * @date 16/5/17 13:34
 */
@Path("v1/cache")
@Consumes(MediaType.APPLICATION_JSON)
public interface CtiLinkCacheService {
    @GET
    @Path("reload")
    String reloadCache();
}
