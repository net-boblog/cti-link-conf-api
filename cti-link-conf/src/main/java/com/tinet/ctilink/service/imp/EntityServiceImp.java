package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.model.Entity;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.service.EntityService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 13:45
 */
@Service
public class EntityServiceImp extends BaseService<Entity>
        implements EntityService {

    @Autowired
    RedisService redisService;

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
