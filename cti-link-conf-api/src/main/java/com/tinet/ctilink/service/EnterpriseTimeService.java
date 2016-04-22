package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseTime;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    ApiResult createEnterpriseTime(EnterpriseTime enterpriseTime);

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
    ApiResult updateEnterpriseTime(EnterpriseTime enterpriseTime);

    /**
     * 根据企业编号enterpriseId获取时间条件列表
     * @param enterpriseTime
     * @return
     */
    @POST
    @Path("list")
    ApiResult getListEnterpriseTime(EnterpriseTime enterpriseTime);

}
