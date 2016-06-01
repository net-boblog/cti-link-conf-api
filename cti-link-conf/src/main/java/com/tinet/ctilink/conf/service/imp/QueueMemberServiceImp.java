package com.tinet.ctilink.conf.service.imp;

import com.tinet.ctilink.conf.entity.Caller;
import com.tinet.ctilink.conf.mapper.AgentSkillMapper;
import com.tinet.ctilink.conf.mapper.QueueSkillMapper;
import com.tinet.ctilink.conf.model.*;
import com.tinet.ctilink.conf.util.AreaCodeUtil;
import com.tinet.ctilink.conf.util.RouterUtil;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/6/1 13:42
 */
@Service
public class QueueMemberServiceImp extends BaseService<QueueMember> {

    @Autowired
    private AgentSkillMapper agentSkillMapper;

    @Autowired
    private QueueSkillMapper queueSkillMapper;

    //更新座席的QueueMember
    public boolean updateByAgent(Agent agent, String tel, Integer telType) {
        Condition condition = new Condition(QueueMember.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", agent.getEnterpriseId());
        criteria.andEqualTo("agentId", agent.getId());
        List<QueueMember> queueMemberList = selectByCondition(condition);
        if (queueMemberList != null) {
            for(QueueMember queueMember : queueMemberList) {
                assembleByTel(queueMember, tel, telType);
                updateByPrimaryKeySelective(queueMember);
            }
        }

        return  true;
    }

    //更新QueueMember
    public void assembleByTel(QueueMember queueMember, String tel, Integer telType) {
        //查询enterpriseRouter
        Integer enterpriseId = queueMember.getEnterpriseId();

        String destInterface = "";
        if (telType == Const.TEL_TYPE_EXTEN) {
            Gateway gateway = RouterUtil.getRouterGatewayInternal(enterpriseId, Const.ROUTER_CLID_CALL_TYPE_IB_RIGHT, tel);
            if(gateway != null){
                destInterface = "PJSIP/" + gateway.getName()+"/sip:" + enterpriseId + tel + "@"
                        + gateway.getIpAddr() + ":" + gateway.getPort();
            }
        } else {
            Caller caller = AreaCodeUtil.updateGetAreaCode(tel, "");
            Gateway gateway = RouterUtil.getRouterGateway(enterpriseId, Const.ROUTER_CLID_CALL_TYPE_IB_RIGHT, caller);
            if (gateway != null) {
                destInterface = "PJSIP/" + gateway.getName()+"/sip:"+gateway.getPrefix() + caller.getCallerNumber() + "@"
                        + gateway.getIpAddr() + ":" + gateway.getPort();
            }
        }

        queueMember.setTel(tel);
        queueMember.setInterface(destInterface);
        queueMember.setPenalty(getPenalty(enterpriseId, queueMember.getQueueId(), queueMember.getAgentId()));
    }


    /**
     * 计算优先级(多个座席在同一个队列A，其中某个座席在队列A中的接听电话优先级)
     */
    public int getPenalty(int enterpriseId, int queueId, int agentId) {
        int penalty = 0;

        Condition condition = new Condition(QueueMember.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        criteria.andEqualTo("queueId", queueId);
        //查询队列所有技能
        List<QueueSkill> queueSkillList = queueSkillMapper.selectByCondition(condition);
        if (queueSkillList == null || queueSkillList.isEmpty()) {
            return penalty;
        }
        //如果只有一个技能, 直接使用座席的技能值
        if (queueSkillList.size() == 1) {
            int skillId = queueSkillList.get(0).getSkillId();
            Condition condition1 = new Condition(AgentSkill.class);
            Condition.Criteria criteria1 = condition1.createCriteria();
            criteria1.andEqualTo("enterpriseId", enterpriseId);
            criteria1.andEqualTo("agentId", agentId);
            criteria1.andEqualTo("skillId", skillId);
            List<AgentSkill> agentSkillList = agentSkillMapper.selectByCondition(condition1);
            if (agentSkillList != null && agentSkillList.size() > 0) {
                penalty = agentSkillList.get(0).getSkillLevel();
            }
        } else {  //多个技能 ?
            Condition condition1 = new Condition(AgentSkill.class);
            Condition.Criteria criteria1 = condition1.createCriteria();
            criteria1.andEqualTo("enterpriseId", enterpriseId);
            criteria1.andEqualTo("agentId", agentId);
            List<AgentSkill> agentSkillList = agentSkillMapper.selectByCondition(condition1);
            for (AgentSkill agentSkill : agentSkillList) {
                penalty += agentSkill.getSkillLevel();
            }
        }
        return penalty;
    }
}
