package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.model.SystemSetting;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author huangbin //
 * @date 16/4/21 14:30
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-db%E5%B9%B3%E5%8F%B0%E8%AE%BE%E7%BD%AE%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/systemSetting")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkSystemSettingService {

    @POST
    @Path("update")
    CtiLinkApiResult<SystemSetting> updateSystemSetting(SystemSetting systemSetting);

    @POST
    @Path("list")
    CtiLinkApiResult<List<SystemSetting>> listSystemSetting(SystemSetting systemSetting);

}
