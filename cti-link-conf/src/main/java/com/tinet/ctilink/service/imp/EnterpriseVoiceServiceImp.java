package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseVoice;
import com.tinet.ctilink.service.EnterpriseVoiceService;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.File;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:20
 */
@Service
public class EnterpriseVoiceServiceImp implements EnterpriseVoiceService {

    @Override
    public ApiResult<EnterpriseVoice> create(MultipartFormDataInput input) {
        return null;
    }

    @Override
    public ApiResult<EnterpriseVoice> create(File file, EnterpriseVoice enterpriseVoice) {
        return null;
    }

    @Override
    public ApiResult delete(EnterpriseVoice enterpriseVoice) {
        return null;
    }

    @Override
    public ApiResult<EnterpriseVoice> update(MultipartFormDataInput input) {
        return null;
    }

    @Override
    public ApiResult<EnterpriseVoice> update(File file, EnterpriseVoice enterpriseVoice) {
        return null;
    }

    @Override
    public ApiResult<List<EnterpriseVoice>> list(EnterpriseVoice enterpriseVoice) {
        return null;
    }
}
