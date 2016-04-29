package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.AgentSkill;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/14 11:25
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E5%BA%A7%E5%B8%AD%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/agentSkill")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AgentSkillService {

    @POST
    @Path("create")
    ApiResult<AgentSkill> createAgentSkill(AgentSkill agentSkill);

    @POST
    @Path("delete")
    ApiResult deleteAgentSkill(AgentSkill agentSkill);

    @POST
    @Path("update")
    ApiResult<AgentSkill> updateAgentSkill(AgentSkill agentSkill);

    @POST
    @Path("list")
    ApiResult<List<AgentSkill>> listAgentSkill(AgentSkill agentSkill);

}
