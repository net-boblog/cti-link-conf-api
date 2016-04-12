package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.Entity;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 13:50
 */
@Path("v1/enterprise")
public interface EntityService {
    @POST
    @Path("list")
    ApiResult<List<Entity>> list();
}
