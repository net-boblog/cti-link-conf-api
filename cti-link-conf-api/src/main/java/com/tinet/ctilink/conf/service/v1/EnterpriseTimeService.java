package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseTime;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author  huangbin
 * @date 2016/4/18
 */

@Path("v1/enterpriseTime")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnterpriseTimeService {
    /**
     * 新增时间条件
     * @param enterpriseTime
     * @return
     */
    @POST
    @Path("create")
    ApiResult<EnterpriseTime> createEnterpriseTime(EnterpriseTime enterpriseTime);

    /**
     * 根据时间条件id和企业编号enterpriseId删除时间条件
     * @param enterpriseTime
     * @return
     */
    @POST
    @Path("delete")
    ApiResult deleteEnterpriseTime(EnterpriseTime enterpriseTime);

    /**
     * 更新时间条件
     * @param enterpriseTime
     * @return
     */
    @POST
    @Path("update")
    ApiResult<EnterpriseTime> updateEnterpriseTime(EnterpriseTime enterpriseTime);

    /**
     * 根据企业编号enterpriseId获取时间条件列表
     * @param enterpriseTime
     * @return
     */
    @POST
    @Path("list")
    ApiResult<List<EnterpriseTime>> listEnterpriseTime(EnterpriseTime enterpriseTime);

}