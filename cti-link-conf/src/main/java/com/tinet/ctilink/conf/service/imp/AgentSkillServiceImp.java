package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.conf.dao.AgentDao;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.dao.SkillDao;
import com.tinet.ctilink.conf.model.Agent;
import com.tinet.ctilink.conf.model.AgentSkill;
import com.tinet.ctilink.conf.model.Skill;
import com.tinet.ctilink.conf.service.v1.AgentSkillService;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/19 17:47
 */
@Service
public class AgentSkillServiceImp extends BaseService<AgentSkill> implements AgentSkillService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private AgentDao agentDao;

    @Autowired
    private SkillDao skillDao;

    @Override
    public ApiResult<AgentSkill> createAgentSkill(AgentSkill agentSkill) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agentSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agentSkill.getAgentId() == null || agentSkill.getAgentId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[agentId]不正确");
        }
        if (agentSkill.getSkillId() == null || agentSkill.getSkillId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[skillId]不正确");
        }
        if (agentSkill.getSkillLevel() == null || agentSkill.getSkillLevel() <= 0
                || agentSkill.getSkillLevel() > 5) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[skillLevel]不正确");
        }
        //TODO 座席在线
        //判断agentId
        Condition agentCondition = new Condition(Agent.class);
        Condition.Criteria agentCriteria = agentCondition.createCriteria();
        agentCriteria.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        agentCriteria.andEqualTo("id", agentSkill.getAgentId());
        int count = agentDao.selectCountByCondition(agentCondition);
        if (count != 1) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[agentId]不正确，座席不存在");
        }
        //判断skillId
        Condition skillCondition = new Condition(Skill.class);
        Condition.Criteria skillCriteria = skillCondition.createCriteria();
        skillCriteria.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        skillCriteria.andEqualTo("id", agentSkill.getSkillId());
        count = skillDao.selectCountByCondition(skillCondition);
        if (count != 1) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[skillId]不正确，座席不存在");
        }
        //座席是否已经有此技能
        Condition condition = new Condition(AgentSkill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("agentId", agentSkill.getAgentId());
        criteria.andEqualTo("skillId", agentSkill.getSkillId());
        count = selectCountByCondition(condition);
        if (count > 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败，座席已经有此技能");
        }

        agentSkill.setCreateTime(new Date());
        count = insertSelective(agentSkill);

        if (count != 1) {  //新增失败
            logger.error("AgentSkillServiceImp.createAgentSkill error, " + agentSkill + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
        }

        return new ApiResult<>(agentSkill);
    }

    @Override
    public ApiResult deleteAgentSkill(AgentSkill agentSkill) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agentSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        //TODO 座席在线

        Condition condition = new Condition(AgentSkill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        criteria.andEqualTo("id", agentSkill.getId());
        int count = deleteByCondition(condition);

        if (count != 1) {
            logger.error("AgentSkillServiceImp.deleteAgentSkill error, " + agentSkill + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "删除失败");
        }

        return new ApiResult(ApiResult.SUCCESS_RESULT);
    }

    @Override
    public ApiResult<AgentSkill> updateAgentSkill(AgentSkill agentSkill) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agentSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agentSkill.getId() == null || agentSkill.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        if (agentSkill.getSkillLevel() == null || agentSkill.getSkillLevel() <= 0
                || agentSkill.getSkillLevel() > 5) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[skillLevel]不正确");
        }
        AgentSkill dbAgentSkill = selectByPrimaryKey(agentSkill.getId());
        if (dbAgentSkill == null || !agentSkill.getEnterpriseId().equals(dbAgentSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }
        //TODO 座席在线
        dbAgentSkill.getAgentId();

        //只有技能值可以更新
        dbAgentSkill.setSkillLevel(agentSkill.getSkillLevel());
        int count = updateByPrimaryKey(dbAgentSkill);
        if (count != 1) {
            logger.error("AgentSkillServiceImp.updateAgentSkill error, " + dbAgentSkill + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }

        return new ApiResult<>(dbAgentSkill);
    }

    @Override
    public ApiResult<List<AgentSkill>> listAgentSkill(AgentSkill agentSkill) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agentSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agentSkill.getAgentId() == null || agentSkill.getAgentId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[agentId]不能为空");
        }

        Condition condition = new Condition(AgentSkill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", agentSkill.getEnterpriseId());
        criteria.andEqualTo("agentId", agentSkill.getAgentId());
        condition.setOrderByClause("id");
        List<AgentSkill> list = selectByCondition(condition);

        return new ApiResult<>(list);
    }

}
