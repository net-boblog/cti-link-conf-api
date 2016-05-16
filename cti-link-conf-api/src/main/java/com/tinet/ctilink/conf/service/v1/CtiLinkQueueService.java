package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.model.Queue;

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
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E9%98%9F%E5%88%97%E7%AE%A1%E7%90%86%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/queue")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkQueueService {

    @POST
    @Path("create")
    CtiLinkApiResult<Queue> createQueue(Queue queue);

    @POST
    @Path("delete")
    CtiLinkApiResult deleteQueue(Queue queue);

    @POST
    @Path("update")
    CtiLinkApiResult<Queue> updateQueue(Queue queue);

    @POST
    @Path("list")
    CtiLinkApiResult<List<Queue>> listQueue(Queue queue);

    @POST
    @Path("get")
    CtiLinkApiResult<Queue> getQueue(Queue queue);

}
