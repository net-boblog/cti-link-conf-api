package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseVoice;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:40
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E4%BC%81%E4%B8%9A%E8%AF%AD%E9%9F%B3%E5%BA%93%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/enterpriseVoice")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnterpriseVoiceService {

    //新增企业语音
    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    ApiResult<EnterpriseVoice> create(MultipartFormDataInput input);

    //dubbo接口
    ApiResult<EnterpriseVoice> create(File file, EnterpriseVoice enterpriseVoice);

    @POST
    @Path("delete")
    ApiResult delete(EnterpriseVoice enterpriseVoice);

    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    ApiResult<EnterpriseVoice> update(MultipartFormDataInput input);

    ApiResult<EnterpriseVoice> update(File file, EnterpriseVoice enterpriseVoice);

    @POST
    @Path("list")
    ApiResult<List<EnterpriseVoice>> list(EnterpriseVoice enterpriseVoice);

}
