package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseVoice;
import com.tinet.ctilink.service.AbstractService;
import com.tinet.ctilink.service.EnterpriseVoiceService;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.File;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:20
 */
@Service
public class EnterpriseVoiceServiceImp extends AbstractService<EnterpriseVoice> implements EnterpriseVoiceService {

    @Override
    public ApiResult<EnterpriseVoice> createEnterpriseVoice(MultipartFormDataInput input) {
        return null;
    }

    @Override
    public ApiResult<EnterpriseVoice> createEnterpriseVoice(File file, EnterpriseVoice enterpriseVoice) {
        return null;
    }

    @Override
    public ApiResult deleteEnterpriseVoice(EnterpriseVoice enterpriseVoice) {
        return null;
    }

    @Override
    public ApiResult<EnterpriseVoice> updateEnterpriseVoice(MultipartFormDataInput input) {
        return null;
    }

    @Override
    public ApiResult<EnterpriseVoice> updateEnterpriseVoice(File file, EnterpriseVoice enterpriseVoice) {
        return null;
    }

    @Override
    public ApiResult<List<EnterpriseVoice>> listEnterpriseVoice(EnterpriseVoice enterpriseVoice) {
        return null;
    }

    @Override
    protected List<EnterpriseVoice> select(Integer enterpriseId) {
        return null;
    }

    @Override
    protected String getKey(EnterpriseVoice enterpriseVoice) {
        return null;
    }

    @Override
    protected String getCleanKeyPrefix() {
        return null;
    }

    @Override
    protected String getRefreshKeyPrefix(Integer enterpriseId) {
        return null;
    }
}
