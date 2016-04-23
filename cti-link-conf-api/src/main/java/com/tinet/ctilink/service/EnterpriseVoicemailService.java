package com.tinet.ctilink.service;

import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseVoicemail;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**@author huangbin
 * @date 2016/4/22.
 */

@Path("v1/enterpriseVoicemail")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnterpriseVoicemailService {
    /**
     * 新增留言箱
     * @param enterpriseVoicemail
     * @return
     */
    @POST
    @Path("create")
    ApiResult createEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail);

    /**
     * 根据留言箱id和企业编号enterpriseId删除留言箱
     * @param enterpriseVoicemail
     * @return
     */
    @POST
    @Path("delete")
    ApiResult deleteEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail);

    /**
     * 更新留言箱
     * @param enterpriseVoicemail
     * @return
     */
    @POST
    @Path("update")
    ApiResult updateEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail);

    /**
     * 根据企业编号enterpriseId获取留言箱列表
     * @param enterpriseVoicemail
     * @return
     */
    @POST
    @Path("list")
    ApiResult getListEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail);

}
