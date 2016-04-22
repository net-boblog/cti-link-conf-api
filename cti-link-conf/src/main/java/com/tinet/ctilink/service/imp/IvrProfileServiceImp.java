package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.model.IvrProfile;
import com.tinet.ctilink.service.AbstractService;
import com.tinet.ctilink.service.IvrProfileService;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/22 13:42
 */
@Service
public class IvrProfileServiceImp extends AbstractService<IvrProfile> implements IvrProfileService {


    @Override
    protected List<IvrProfile> select(Integer enterpriseId) {
        return null;
    }

    @Override
    protected String getKey(IvrProfile ivrProfile) {
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
