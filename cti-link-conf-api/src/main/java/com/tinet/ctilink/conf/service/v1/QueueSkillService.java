package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.conf.model.QueueSkill;

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
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E9%98%9F%E5%88%97%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1#6%E9%98%9F%E5%88%97%E6%96%B0%E5%A2%9E%E6%8A%80%E8%83%BD
 */
@Path("v1/queueSkill")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface QueueSkillService {

    @POST
    @Path("create")
    ApiResult<QueueSkill> createQueueSkill(QueueSkill queueSkill);

    @POST
    @Path("delete")
    ApiResult<QueueSkill> deleteQueueSkill(QueueSkill queueSkill);

    @POST
    @Path("update")
    ApiResult<QueueSkill> updateQueueSkill(QueueSkill queueSkill);

    @POST
    @Path("list")
    ApiResult<List<QueueSkill>> listQueueSkill(QueueSkill queueSkill);

}
