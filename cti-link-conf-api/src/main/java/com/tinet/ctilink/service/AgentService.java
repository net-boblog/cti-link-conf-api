package com.tinet.ctilink.service;


import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.Agent;
import com.tinet.ctilink.request.AgentListRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 14:28
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E5%BA%A7%E5%B8%AD%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1
 *
 * 版本控制 v1 ?
 * PATH能不能自动生成 ?
 */
@Path("v1/agent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AgentService {

    @POST
    @Path("create")
    ApiResult<Agent> create(Agent agent);

    @POST
    @Path("delete")
    ApiResult deleteById(Agent agent);

    @POST
    @Path("update")
    ApiResult<Agent> update(Agent agent);

    @POST
    @Path("list")
    ApiResult<List<Agent>> list(AgentListRequest agent);

    @POST
    @Path("get")
    ApiResult<Agent> get(Agent agent);
}

