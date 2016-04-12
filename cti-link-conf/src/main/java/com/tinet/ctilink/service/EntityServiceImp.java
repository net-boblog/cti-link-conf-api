package com.tinet.ctilink.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.model.Entity;
import org.springframework.beans.factory.InitializingBean;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 13:45
 */
@Service
public class EntityServiceImp extends BaseService<Entity>
        implements EntityService, InitializingBean {

    @Override
    public ApiResult<List<Entity>> list() {
        ApiResult<List<Entity>> result = new ApiResult<>();
        Condition condition = new Condition(Entity.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andNotEqualTo("status", Const.ENTITY_STATUS_CLOSE);
        List<Entity> list = selectByCondition(condition);

        result.setData(list);

        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
