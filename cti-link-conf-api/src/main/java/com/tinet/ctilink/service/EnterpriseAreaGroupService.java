package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseAreaGroup;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author  huangbin
 * @date 2016/4/18.
 */

@Path("v1/enterpriseAreaGroup")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public interface EnterpriseAreaGroupService {
    /**
     * 新增地区组
     * @param enterpriseAreaGroup
     * @return
     */
    @POST
    @Path("create")
    ApiResult createEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) ;

    /**
     * 根据地区组id和企业编号enterpriseId删除地区组
     * @param enterpriseAreaGroup
     * @return
     */
    @POST
    @Path("delete")
    ApiResult deleteEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup);

    /**
     * 更新地区组
     * @param enterpriseAreaGroup
     * @return
     */
    @POST
    @Path("update")
    ApiResult updateEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup);

    /**
     * 根据企业编号enterpriseId获取地区组列表
     * @param enterpriseAreaGroup
     * @return
     */
    @POST
    @Path("list")
    ApiResult getListEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup);
}
