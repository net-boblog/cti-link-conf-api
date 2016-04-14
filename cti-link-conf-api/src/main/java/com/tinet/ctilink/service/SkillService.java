package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.Skill;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:42
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E6%8A%80%E8%83%BD%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/skill")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SkillService {

    @POST
    @Path("create")
    ApiResult<Skill> create(Skill skill);

    @POST
    @Path("delete")
    ApiResult delete(Skill skill);

    @POST
    @Path("update")
    ApiResult<Skill> update(Skill skill);

    @POST
    @Path("list")
    ApiResult<List<Skill>> list(Skill skill);
}
