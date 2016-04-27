package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.conf.service.v1.EntityService;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 13:45
 */
@Service
public class EntityServiceImp extends BaseService<Entity>
        implements EntityService {

    @Override
    public ApiResult<List<Entity>> list() {
        Condition condition = new Condition(Entity.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andNotEqualTo("status", Const.ENTITY_STATUS_CLOSE);
        List<Entity> list = selectByCondition(condition);

        return new ApiResult<>(list);
    }

    @Override
    public ApiResult<Entity> get(Entity entity) {
        return null;
    }



}
