package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.dao.*;
import com.tinet.ctilink.conf.model.*;
import com.tinet.ctilink.conf.service.v1.CtiLInkAgentSkillService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.rm.entity.Caller;
import com.tinet.ctilink.rm.util.AreaCodeUtil;
import com.tinet.ctilink.rm.util.RouterUtil;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/19 17:47
 */
@Service
public class CtiLInkAgentSkillServiceImp extends BaseService<AgentSkill> implements CtiLInkAgentSkillService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private AgentDao agentDao;

    @Autowired
    private AgentTelDao agentTelDao;

    @Autowired
    private SkillDao skillDao;

    @Autowired
    private QueueSkillDao queueSkillDao;

    @Autowired
    private QueueMemberDao queueMemberDao;

    @Autowired
    private QueueDao queueDao;

    @Override
    public CtiLinkApiResult<AgentSkill> createAgentSkill(AgentSkill agentSkill) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agentSkill.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agentSkill.getAgentId() == null || agentSkill.getAgentId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[agentId]不正确");
        }
        if (agentSkill.getSkillId() == null || agentSkill.getSkillId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[skillId]不正确");
        }
        if (agentSkill.getSkillLevel() == null || agentSkill.getSkillLevel() <= 0
                || agentSkill.getSkillLevel() > 5) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[skillLevel]不正确");
        }
        //TODO 座席在线
        //判断agentId
        Condition agentCondition = new Condition(Agent.class);
        Condition.Criteria agentCriteria = agentCondition.createCriteria();
        agentCriteria.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        agentCriteria.andEqualTo("id", agentSkill.getAgentId());
        List<Agent> agentList = agentDao.selectByCondition(agentCondition);
        if (agentList == null || agentList.size() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[agentId]不正确，座席不存在");
        }
        //判断skillId
        Condition skillCondition = new Condition(Skill.class);
        Condition.Criteria skillCriteria = skillCondition.createCriteria();
        skillCriteria.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        skillCriteria.andEqualTo("id", agentSkill.getSkillId());
        int count = skillDao.selectCountByCondition(skillCondition);
        if (count != 1) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[skillId]不正确，座席不存在");
        }
        //座席是否已经有此技能
        Condition condition = new Condition(AgentSkill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("agentId", agentSkill.getAgentId());
        criteria.andEqualTo("skillId", agentSkill.getSkillId());
        count = selectCountByCondition(condition);
        if (count > 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "新增失败，座席已经有此技能");
        }

        agentSkill.setCreateTime(new Date());
        count = insertSelective(agentSkill);

        if (count != 1) {  //新增失败
            logger.error("CtiLInkAgentSkillServiceImp.createAgentSkill error, " + agentSkill + ", count=" + count);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "新增失败");
        }

        //TODO 提出来, 减少SQL操作？
        //新增或更新queue_member
        Condition condition1 = new Condition(QueueSkill.class);
        Condition.Criteria criteria1 = condition1.createCriteria();
        criteria1.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        criteria1.andEqualTo("skillId", agentSkill.getSkillId());
        criteria1.andGreaterThanOrEqualTo("skillLevel", agentSkill.getSkillLevel());
        List<QueueSkill> queueSkillList = queueSkillDao.selectByCondition(condition1);

        Condition condition2 = new Condition(QueueMember.class);
        Condition.Criteria criteria2 = condition2.createCriteria();
        criteria2.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        criteria2.andEqualTo("agentId", agentSkill.getAgentId());
        List<QueueMember> queueMemberList = queueMemberDao.selectByCondition(condition2);
        List<QueueSkill> insertQueueSkillList = new ArrayList<>();
        for (QueueSkill queueSkill : queueSkillList) {
            boolean flag = true;
            for (QueueMember queueMember : queueMemberList) {
                if (queueMember.getQueueId().equals(queueSkill.getQueueId())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                insertQueueSkillList.add(queueSkill);
            }
        }
        if (insertQueueSkillList.size() > 0) {
            for (QueueSkill queueSkill : insertQueueSkillList) {
                AgentTel agentTel = agentTelDao.getBindTel(agentSkill.getAgentId());
                Queue queue = queueDao.selectByPrimaryKey(queueSkill.getQueueId());
                QueueMember queueMember = new QueueMember();
                queueMember.setEnterpriseId(agentSkill.getEnterpriseId());
                queueMember.setAgentId(agentSkill.getAgentId());
                queueMember.setCno(agentList.get(0).getCno());
                queueMember.setCreateTime(new Date());
                queueMember.setQueueId(queueSkill.getQueueId());
                queueMember.setQno(queue.getQno());
                queueMember.setPenalty(getPenalty(queue, agentList.get(0)));
                if (agentTel != null) {
                    Caller caller;
                    if (agentTel.getTelType() == Const.TEL_TYPE_EXTEN
                            || agentTel.getTelType() == Const.TEL_TYPE_SIP) {
                        caller = new Caller(agentTel.getTel());
                        caller.setTelType(Const.TEL_TYPE_EXTEN);
                    } else {
                        caller = AreaCodeUtil.updateGetAreaCode(agentTel.getTel(), "");
                    }
                    Gateway gateway = RouterUtil.getRouterGateway(agentTel.getEnterpriseId(), Const.ROUTER_CLID_CALL_TYPE_IB_RIGHT, caller);
                    String itf;
                    if (agentTel.getTelType() == Const.TEL_TYPE_EXTEN
                            || agentTel.getTelType() == Const.TEL_TYPE_SIP) {
                        itf = "SIP/" + agentTel.getEnterpriseId() + "-" + agentTel.getTel();
                    } else {
                        itf = "SIP/" + (gateway == null ? "" : gateway.getPrefix()) + caller.getCallerNumber() + "@"
                                + agentTel.getEnterpriseId() + queueMember.getCno();
                    }
                    queueMember.setTel(agentTel.getTel());
                    queueMember.setInterface(itf);
                } else {
                    queueMember.setTel("");
                    queueMember.setInterface("");
                }

                queueMemberDao.insertSelective(queueMember);
            }
        }

        return new CtiLinkApiResult<>(agentSkill);
    }

    private int getPenalty(Queue queue, Agent agent) {
        int penalty = 0;
        Condition condition = new Condition(QueueMember.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", queue.getEnterpriseId());
        criteria.andEqualTo("queueId", queue.getId());
        List<QueueSkill> queueSkillList = queueSkillDao.selectByCondition(condition);
        if (queueSkillList == null || queueSkillList.isEmpty()) {
            return penalty;
        }
        if (queueSkillList.size() == 1) {
            int skillId = queueSkillList.get(0).getSkillId();

            Condition condition1 = new Condition(AgentSkill.class);
            Condition.Criteria criteria1 = condition1.createCriteria();
            criteria1.andEqualTo("enterpriseId", agent.getEnterpriseId());
            criteria1.andEqualTo("agentId", agent.getId());
            criteria1.andEqualTo("skillId", skillId);
            List<AgentSkill> agentSkillList = selectByCondition(condition1);
            if (agentSkillList != null && agentSkillList.size() > 0) {
                penalty = agentSkillList.get(0).getSkillLevel();
            }

        } else {
            Condition condition1 = new Condition(AgentSkill.class);
            Condition.Criteria criteria1 = condition1.createCriteria();
            criteria1.andEqualTo("enterpriseId", agent.getEnterpriseId());
            criteria1.andEqualTo("agentId", agent.getId());
            List<AgentSkill> agentSkillList = selectByCondition(condition1);
            for (AgentSkill agentSkill : agentSkillList) {
                penalty += agentSkill.getSkillLevel();
            }
        }

        return penalty;
    }

    @Override
    public CtiLinkApiResult deleteAgentSkill(AgentSkill agentSkill) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agentSkill.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        //TODO 座席在线

        Condition condition = new Condition(AgentSkill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        criteria.andEqualTo("id", agentSkill.getId());
        int count = deleteByCondition(condition);

        if (count != 1) {
            logger.error("CtiLInkAgentSkillServiceImp.deleteAgentSkill error, " + agentSkill + ", count=" + count);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "删除失败");
        }


        Agent agent = agentDao.selectByPrimaryKey(agentSkill.getAgentId());
        //TODO 提出来, 减少SQL操作？
        //新增或更新queue_member
        Condition condition1 = new Condition(QueueSkill.class);
        Condition.Criteria criteria1 = condition1.createCriteria();
        criteria1.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        criteria1.andEqualTo("skillId", agentSkill.getSkillId());
        criteria1.andGreaterThanOrEqualTo("skillLevel", agentSkill.getSkillLevel());
        List<QueueSkill> queueSkillList = queueSkillDao.selectByCondition(condition1);

        Condition condition2 = new Condition(QueueMember.class);
        Condition.Criteria criteria2 = condition2.createCriteria();
        criteria2.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        criteria2.andEqualTo("agentId", agentSkill.getAgentId());
        List<QueueMember> queueMemberList = queueMemberDao.selectByCondition(condition2);
        List<QueueSkill> insertQueueSkillList = new ArrayList<>();
        for (QueueSkill queueSkill : queueSkillList) {
            boolean flag = true;
            for (QueueMember queueMember : queueMemberList) {
                if (queueMember.getQueueId().equals(queueSkill.getQueueId())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                insertQueueSkillList.add(queueSkill);
            }
        }
        if (insertQueueSkillList.size() > 0) {
            for (QueueSkill queueSkill : insertQueueSkillList) {
                AgentTel agentTel = agentTelDao.getBindTel(agentSkill.getAgentId());
                Queue queue = queueDao.selectByPrimaryKey(queueSkill.getQueueId());
                QueueMember queueMember = new QueueMember();
                queueMember.setEnterpriseId(agentSkill.getEnterpriseId());
                queueMember.setAgentId(agentSkill.getAgentId());
                queueMember.setCno(agent.getCno());
                queueMember.setCreateTime(new Date());
                queueMember.setQueueId(queueSkill.getQueueId());
                queueMember.setQno(queue.getQno());
                queueMember.setPenalty(getPenalty(queue, agent));
                if (agentTel != null) {
                    Caller caller;
                    if (agentTel.getTelType() == Const.TEL_TYPE_EXTEN
                            || agentTel.getTelType() == Const.TEL_TYPE_SIP) {
                        caller = new Caller(agentTel.getTel());
                        caller.setTelType(Const.TEL_TYPE_EXTEN);
                    } else {
                        caller = AreaCodeUtil.updateGetAreaCode(agentTel.getTel(), "");
                    }
                    Gateway gateway = RouterUtil.getRouterGateway(agentTel.getEnterpriseId(), Const.ROUTER_CLID_CALL_TYPE_IB_RIGHT, caller);
                    String itf;
                    if (agentTel.getTelType() == Const.TEL_TYPE_EXTEN
                            || agentTel.getTelType() == Const.TEL_TYPE_SIP) {
                        itf = "SIP/" + agentTel.getEnterpriseId() + "-" + agentTel.getTel();
                    } else {
                        itf = "SIP/" + (gateway == null ? "" : gateway.getPrefix()) + caller.getCallerNumber() + "@"
                                + agentTel.getEnterpriseId() + queueMember.getCno();
                    }
                    queueMember.setTel(agentTel.getTel());
                    queueMember.setInterface(itf);
                } else {
                    queueMember.setTel("");
                    queueMember.setInterface("");
                }

                queueMemberDao.insertSelective(queueMember);
            }
        }

        return new CtiLinkApiResult(CtiLinkApiResult.SUCCESS_RESULT);
    }

    @Override
    public CtiLinkApiResult<AgentSkill> updateAgentSkill(AgentSkill agentSkill) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agentSkill.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agentSkill.getId() == null || agentSkill.getId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        if (agentSkill.getSkillLevel() == null || agentSkill.getSkillLevel() <= 0
                || agentSkill.getSkillLevel() > 5) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[skillLevel]不正确");
        }
        AgentSkill dbAgentSkill = selectByPrimaryKey(agentSkill.getId());
        if (dbAgentSkill == null || !agentSkill.getEnterpriseId().equals(dbAgentSkill.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }
        //TODO 座席在线
        dbAgentSkill.getAgentId();

        //只有技能值可以更新
        dbAgentSkill.setSkillLevel(agentSkill.getSkillLevel());
        int count = updateByPrimaryKey(dbAgentSkill);
        if (count != 1) {
            logger.error("CtiLInkAgentSkillServiceImp.updateAgentSkill error, " + dbAgentSkill + ", count=" + count);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "更新失败");
        }

        //TODO 更新QueueMember的penalty ?

        return new CtiLinkApiResult<>(dbAgentSkill);
    }

    @Override
    public CtiLinkApiResult<List<AgentSkill>> listAgentSkill(AgentSkill agentSkill) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agentSkill.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agentSkill.getAgentId() == null || agentSkill.getAgentId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[agentId]不能为空");
        }

        Condition condition = new Condition(AgentSkill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        criteria.andEqualTo("agentId", agentSkill.getAgentId());
        condition.setOrderByClause("id");
        List<AgentSkill> list = selectByCondition(condition);

        return new CtiLinkApiResult<>(list);
    }

}
