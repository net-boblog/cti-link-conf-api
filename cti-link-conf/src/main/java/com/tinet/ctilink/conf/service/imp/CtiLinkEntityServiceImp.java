package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.conf.service.v1.CtiLinkEntityService;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 13:45
 */
@Service
public class CtiLinkEntityServiceImp extends BaseService<Entity>
        implements CtiLinkEntityService {

    @Override
    public CtiLinkApiResult<List<Entity>> listEntity() {
        Condition condition = new Condition(Entity.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andNotEqualTo("status", Const.ENTITY_STATUS_CLOSE);
        List<Entity> list = selectByCondition(condition);

        return new CtiLinkApiResult<>(list);
    }

    @Override
    public CtiLinkApiResult<Entity> getEntity(Entity entity) {
        if (entity.getEnterpriseId() == null || entity.getEnterpriseId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        Condition condition = new Condition(Entity.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", entity.getEnterpriseId());
        criteria.andNotEqualTo("status", Const.ENTITY_STATUS_CLOSE);
        List<Entity> list = selectByCondition(condition);
        Entity e = null;
        if (list != null) {
            e = list.get(0);
        }
        return new CtiLinkApiResult<>(e);
    }



}
