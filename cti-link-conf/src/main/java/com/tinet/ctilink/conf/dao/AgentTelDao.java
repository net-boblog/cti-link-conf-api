package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.conf.model.AgentTel;
import com.tinet.ctilink.inc.Const;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/20 11:30
 */
@Repository
public class AgentTelDao extends BaseDao<AgentTel> {

    public boolean updateAgentTelBind(Integer agentId, String tel) {
        //update client_tel set is_bind = (case when tel=? then 1 else 0 end) where client_id = ?

        return true;
    }

    public AgentTel getBindTel (Integer agentId) {
        Condition condition = new Condition(AgentTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("agentId", agentId);
        criteria.andEqualTo("idBind", Const.AGENT_TEL_IS_BIND_YES);
        List<AgentTel> list = selectByCondition(condition);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
