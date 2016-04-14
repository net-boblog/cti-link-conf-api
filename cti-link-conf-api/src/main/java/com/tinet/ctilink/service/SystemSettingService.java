package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.SystemSetting;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/14 14:30
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-db%E5%B9%B3%E5%8F%B0%E8%AE%BE%E7%BD%AE%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/systemSetting")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SystemSettingService {

    @POST
    @Path("update")
    ApiResult<SystemSetting> update(SystemSetting systemSetting);

    @POST
    @Path("list")
    ApiResult<List<SystemSetting>> list();

}
