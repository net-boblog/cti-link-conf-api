package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.model.Entity;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/20 11:29
 */
@Repository
public class EntityDao extends BaseDao<Entity> {

    public boolean validateEntity(Integer enterpriseId) {
        if (enterpriseId == null || enterpriseId <= 0) {
            return false;
        }
        Condition condition = new Condition(Entity.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        criteria.andNotEqualTo("status", Const.ENTITY_STATUS_CLOSE);
        List<Entity> list = selectByCondition(condition);
        if (list == null || list.size() <= 0) {
            return false;
        }

        return true;
    }
}
