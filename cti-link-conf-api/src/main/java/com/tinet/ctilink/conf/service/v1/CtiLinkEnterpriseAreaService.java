package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseArea;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author  huangbin //
 * @date 2016/4/18. //
 */

@Path("v1/enterpriseArea")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkEnterpriseAreaService {
    /**
     * 地区组新增地区
     * @param enterpriseArea
     * @return
     */
    @POST
    @Path("create")
    ApiResult<EnterpriseArea> createEnterpriseArea(EnterpriseArea enterpriseArea);

    /**
     * 地区组根据根据企业编号enterpriseId和地区id删除地区
     * @param enterpriseArea
     * @return
     */
    @POST
    @Path("delete")
    ApiResult deleteEnterpriseArea(EnterpriseArea enterpriseArea);

    /**
     * 地区组根据企业编号enterpriseId和地区组id获取地区列表
     * @param enterpriseArea
     * @return
     */
    @POST
    @Path("list")
    ApiResult<List<EnterpriseArea>> listEnterpriseArea(EnterpriseArea enterpriseArea);

}