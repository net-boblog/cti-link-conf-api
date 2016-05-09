package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.AgentTel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/14 11:41
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E5%BA%A7%E5%B8%AD%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1#9%E5%BA%A7%E5%B8%AD%E6%96%B0%E5%A2%9E%E7%94%B5%E8%AF%9D
 */
@Path("v1/agentTel")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkAgentTelService {

    @POST
    @Path("create")
    ApiResult<AgentTel> createAgentTel(AgentTel agentTel);

    @POST
    @Path("delete")
    ApiResult deleteAgentTel(AgentTel agentTel);

    @POST
    @Path("update")
    ApiResult<AgentTel> updateAgentTel(AgentTel agentTel);

    @POST
    @Path("list")
    ApiResult<List<AgentTel>> listAgentTel(AgentTel agentTel);
}
