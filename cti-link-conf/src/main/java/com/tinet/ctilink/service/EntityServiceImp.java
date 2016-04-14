package com.tinet.ctilink.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.model.Entity;
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
        implements EntityService, InitializingBean {

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

    @Override
    public ApiResult<Entity> create(Entity entity) {

        insertSelective(entity);
        redisService.set("cache_test1", "create");
        return new ApiResult<>(entity);
    }

    @Override
    public ApiResult<Entity> createWithSleep(Entity entity) {
        insertSelective(entity);
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisService.set("cache_test2", "createWithSleep");
        return new ApiResult<>(entity);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
