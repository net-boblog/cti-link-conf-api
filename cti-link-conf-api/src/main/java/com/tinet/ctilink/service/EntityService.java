package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.Entity;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 13:50
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E4%BC%81%E4%B8%9A%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/entity")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EntityService {
    @POST
    @Path("list")
    ApiResult<List<Entity>> list();

    @POST
    @Path("get")
    ApiResult<Entity> get(Entity entity);


    @POST
    @Path("create")
    ApiResult<Entity> create(Entity entity);

    @POST
    @Path("create/test")
    ApiResult<Entity> createWithSleep(Entity entity);
}
