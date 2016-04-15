package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.Agent;
import com.tinet.ctilink.request.AgentListRequest;
import com.tinet.ctilink.service.AbstractService;
import com.tinet.ctilink.service.AgentService;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 16:49
 */
@Service
public class AgentServiceImp extends AbstractService<Agent> implements AgentService {

    @Override
    public ApiResult<Agent> create(Agent agent) {
        return null;
    }

    @Override
    public ApiResult deleteById(Agent agent) {
        return new ApiResult();
    }

    @Override
    public ApiResult<Agent> update(Agent agent) {
        return null;
    }

    @Override
    public ApiResult<List<Agent>> list(AgentListRequest agent) {
        return null;
    }

    @Override
    public ApiResult<Agent> get(Agent agent) {
        return null;
    }

    @Override
    protected List<Agent> selectByEnterpriseId(Integer enterpriseId) {
        return null;
    }

    @Override
    protected String getCacheKey(Agent agent) {
        return null;
    }

    @Override
    protected String getCleanCacheKeyPrefix() {
        return null;
    }

    @Override
    protected String getRefreshCacheKeyPrefix(Integer enterpriseId) {
        return null;
    }
}
