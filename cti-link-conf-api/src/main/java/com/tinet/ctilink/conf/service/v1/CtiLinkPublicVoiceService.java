package com.tinet.ctilink.conf.service.v1;

import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.model.PublicVoice;
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
 * @date 16/4/14 11:25
 *
 * https://github.com/ti-net/cti-link-conf-api/wiki/conf-api%E5%85%AC%E5%85%B1%E8%AF%AD%E9%9F%B3%E6%96%87%E4%BB%B6%E4%B8%8A%E4%BC%A0%E6%8E%A5%E5%8F%A3-v1
 */
@Path("v1/publicVoice")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CtiLinkPublicVoiceService {

    @POST
    @Path("list")
    CtiLinkApiResult<List<PublicVoice>> list();

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    CtiLinkApiResult<PublicVoice> create(MultipartFormDataInput input);

    //dubbo接口
    CtiLinkApiResult<PublicVoice> create(File file, PublicVoice publicVoice);

    @POST
    @Path("update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    CtiLinkApiResult<PublicVoice> update(MultipartFormDataInput input);

    //dubbo接口
    CtiLinkApiResult<PublicVoice> update(File file, PublicVoice publicVoice);

    @POST
    @Path("delete")
    CtiLinkApiResult delete(PublicVoice publicVoice);

}
