package com.tinet.ctilink.dao;

import com.tinet.ctilink.model.AgentTel;
import org.springframework.stereotype.Repository;

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
}
