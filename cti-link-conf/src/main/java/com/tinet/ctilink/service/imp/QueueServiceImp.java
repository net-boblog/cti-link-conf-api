package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.Queue;
import com.tinet.ctilink.service.QueueService;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:17
 */
@Service
public class QueueServiceImp implements QueueService {

    @Override
    public ApiResult<Queue> create(Queue queue) {
        return null;
    }

    @Override
    public ApiResult delete(Queue queue) {
        return null;
    }

    @Override
    public ApiResult<Queue> update(Queue queue) {
        return null;
    }

    @Override
    public ApiResult<List<Queue>> list(Queue queue) {
        return null;
    }

    @Override
    public ApiResult<Queue> get(Queue queue) {
        return null;
    }
}
