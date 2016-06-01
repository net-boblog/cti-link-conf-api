package com.tinet.ctilink.conf.mapper;

import com.tinet.ctilink.conf.model.AgentTel;
import com.tinet.ctilink.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * @author fengwei //
 * @date 16/4/11 19:17
 */
@Component
public interface AgentTelMapper extends BaseMapper<AgentTel> {
    /**
     * 更新绑定
     * @param agentTel
     */
    boolean updateBind(AgentTel agentTel);

    /**
     * 查询绑定电话
     * @param agentId
     */
    AgentTel getBindTel (Integer agentId);
}
