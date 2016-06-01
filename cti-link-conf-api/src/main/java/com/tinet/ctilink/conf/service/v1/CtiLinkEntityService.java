package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.request.EntityCreateRequest;
import com.tinet.ctilink.json.JSONArray;
import com.tinet.ctilink.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/4/7 13:50
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E4%BC%81%E4%B8%9A%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/entity")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEntityService {

    @POST
    @Path("create")
    ApiResult<EntityCreateRequest> createEntity(EntityCreateRequest entityCreateRequest);

    @POST
    @Path("update")
    ApiResult<Entity> updateEntity(Entity entity);

    @POST
    @Path("list")
    ApiResult<List<Entity>> listEntity();

    @POST
    @Path("get")
    ApiResult<Entity> getEntity(Entity entity);


    @POST
    @Path("sipGroup/list")
    ApiResult<JSONArray> listEntitySipGroup();


    @POST
    @Path("sipGroup/update")
    ApiResult updateEntitySipGroup(Map<String, Integer> params);
}
