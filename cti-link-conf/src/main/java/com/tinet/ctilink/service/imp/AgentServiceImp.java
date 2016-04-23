package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.dao.AgentSkillDao;
import com.tinet.ctilink.dao.AgentTelDao;
import com.tinet.ctilink.dao.EntityDao;
import com.tinet.ctilink.dao.QueueMemberDao;
import com.tinet.ctilink.filter.AfterReturningMethod;
import com.tinet.ctilink.filter.ProviderFilter;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.model.Agent;
import com.tinet.ctilink.model.AgentSkill;
import com.tinet.ctilink.model.AgentTel;
import com.tinet.ctilink.model.QueueMember;
import com.tinet.ctilink.request.AgentListRequest;
import com.tinet.ctilink.service.AbstractService;
import com.tinet.ctilink.service.AgentService;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/4/7 16:49
 */
@Service
public class AgentServiceImp extends BaseService<Agent> implements AgentService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private AgentSkillDao agentSkillDao;

    @Autowired
    private AgentTelDao agentTelDao;

    @Autowired
    private QueueMemberDao queueMemberDao;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<Agent> createAgent(Agent agent) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agent.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        //校验参数
        agent.setId(null);
        ApiResult<Agent> result = validateAgent(agent);
        if (result != null) {
            return result;
        }

        agent.setCreateTime(new Date());
        int count = insertSelective(agent);
        if (count != 1) {
            logger.error("AgentServiceImp.createAgent error, " + agent + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
        } else {
            setRefreshCacheMethod("setCache", agent);
            return new ApiResult<>(agent);
        }
    }

    @Override
    public ApiResult deleteAgent(Agent agent) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agent.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agent.getId() == null || agent.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        //TODO 检查座席是否可以删除, 是否在线

        //agent删除成功
        //delete queue_member, agent_tel, agent_skill
        Condition qmCondition = new Condition(QueueMember.class);
        Condition.Criteria qmCriteria = qmCondition.createCriteria();
        qmCriteria.andEqualTo("agentId", agent.getId());
        qmCondition.setTableName("cti_link_queue_member");
        queueMemberDao.deleteByCondition(qmCondition);

        Condition atCondition = new Condition(AgentTel.class);
        Condition.Criteria atCriteria = atCondition.createCriteria();
        atCriteria.andEqualTo("agentId", agent.getId());
        atCondition.setTableName("cti_link_agent_tel");
        agentTelDao.deleteByCondition(atCondition);

        Condition asCondition = new Condition(AgentSkill.class);
        Condition.Criteria asCriteria = asCondition.createCriteria();
        asCriteria.andEqualTo("agentId", agent.getId());
        asCondition.setTableName("cti_link_agent_skill");
        agentSkillDao.deleteByCondition(asCondition);

        Condition condition = new Condition(Agent.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", agent.getEnterpriseId());
        criteria.andEqualTo("id", agent.getId());
        int count = deleteByCondition(condition);

        if (count != 1) {
            logger.error("AgentServiceImp.deleteAgent error, " + agent + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "删除失败");
        }
        setRefreshCacheMethod("deleteCache", agent);
        return new ApiResult(ApiResult.SUCCESS_RESULT);
    }

    @Override
    public ApiResult<Agent> updateAgent(Agent agent) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agent.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agent.getId() == null || agent.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        //校验参数
        ApiResult<Agent> result = validateAgent(agent);
        if (result != null) {
            return result;
        }
        //TODO 座席在线不能更新?

        Agent dbAgent = selectByPrimaryKey(agent.getId());
        if (dbAgent == null || !agent.getEnterpriseId().equals(dbAgent.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }
        //不能更改的字段
        agent.setCno(dbAgent.getCno());
        agent.setCreateTime(dbAgent.getCreateTime());
        int count = updateByPrimaryKeySelective(agent);

        if (count != 1) {
            logger.error("AgentServiceImp.updateAgent error, " + agent + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }
        setRefreshCacheMethod("setCache", agent);
        return new ApiResult<>(agent);
    }

    @Override
    public ApiResult<List<Agent>> listAgent(AgentListRequest request) {
        //验证enterpriseId
        if (!entityDao.validateEntity(request.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (request.getLimit() <= 0 || request.getLimit() > 500) {
            request.setLimit(10);
        }
        if (request.getOffset() <= 0) {
            request.setOffset(0);
        }
        PageHelper.startPage(request.getOffset()/request.getLimit() + 1, request.getLimit());

        Condition condition = new Condition(Agent.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", request.getEnterpriseId());
        condition.setOrderByClause("cno");

        if (StringUtils.isNotEmpty(request.getCno())) {
            criteria.andEqualTo("cno", SqlUtil.escapeSql(request.getCno()));
        }
        if (StringUtils.isNotEmpty(request.getCrmId())) {
            criteria.andEqualTo("crmId", SqlUtil.escapeSql(request.getCrmId()));
        }
        if (request.getActive() != null) {
            criteria.andEqualTo("active", request.getActive());
        }
        if (request.getAgentType() != null) {
            criteria.andEqualTo("agentType", request.getAgentType());
        }
        if (request.getCallPower() != null) {
            criteria.andEqualTo("callPower", request.getCallPower());
        }
        if (request.getWrapup() != null) {
            criteria.andEqualTo("wrapup", request.getWrapup());
        }
        if (request.getIsOb() != null) {
            criteria.andEqualTo("isOb", request.getIsOb());
        }
        if (request.getIbRecord() != null) {
            criteria.andEqualTo("ibRecord", request.getIbRecord());
        }
        if (request.getObRecord() != null) {
            criteria.andEqualTo("obRecord", request.getObRecord());
        }

        List<Agent> list = selectByCondition(condition);

        return new ApiResult<>(list);
    }

    @Override
    public ApiResult<Agent> getAgent(Agent agent) {
        //验证enterpriseId
        if (!entityDao.validateEntity(agent.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agent.getId() == null || agent.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        Agent dbAgent = selectByPrimaryKey(agent.getId());
        if (dbAgent == null || !agent.getEnterpriseId().equals(dbAgent.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        return new ApiResult<>(dbAgent);
    }

    //校验agent
    private <T> ApiResult<T> validateAgent(Agent agent) {
        if (StringUtils.isEmpty(agent.getName())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[name]不能为空");
        }
        agent.setName(SqlUtil.escapeSql(agent.getName()));
        if (StringUtils.isEmpty(agent.getAreaCode())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[areaCode]不能为空");
        }
        if (!agent.getAreaCode().matches(Const.AREA_CODE_VALIDATION)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[areaCode]格式不正确");
        }

        //新增座席
        if (agent.getId() == null) {
            if (StringUtils.isEmpty(agent.getCno())) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[cno]不能为空");
            }
            if (agent.getCno().length() < 4 || agent.getCno().length() > 5) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[cno]位数不正确");
            }
            if (!StringUtils.isNumeric(agent.getCno())
                    || Integer.parseInt(agent.getCno()) < 0) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[cno]格式不正确");
            }
            //判断name是否已经存在
            Condition condition = new Condition(Agent.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("enterpriseId", agent.getEnterpriseId());
            criteria.andEqualTo("cno", agent.getCno());
            List<Agent> list = selectByCondition(condition);
            if (list != null && list.size() > 0) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "座席工号已经存在");
            }
            if (StringUtils.isEmpty(agent.getCrmId())) {
                agent.setCrmId("");
            }
        }
        agent.setCrmId(SqlUtil.escapeSql(agent.getCrmId()));

        //处理默认值
        if (agent.getActive() == null ||
                (agent.getActive() != Const.AGENT_ACTIVE_DEFAULT && agent.getActive() != Const.AGENT_ACTIVE_OFF)) {
            if (isCreateAgent(agent)
                    || (!isCreateAgent(agent) && agent.getActive() == null)) {
                agent.setActive(Const.AGENT_ACTIVE_DEFAULT);
            }
        }
        if (agent.getAgentType() == null ||
                (agent.getAgentType() != Const.AGENT_TYPE_TEL && agent.getAgentType() != Const.AGENT_TYPE_WEB)) {
            if (isCreateAgent(agent)
                    || (!isCreateAgent(agent) && agent.getAgentType() == null)) {
                agent.setAgentType(Const.AGENT_TYPE_TEL);
            }
        }
        if (agent.getCallPower() == null ||
                (agent.getCallPower() != Const.AGENT_CALL_POWER_ALL && agent.getCallPower() != Const.AGENT_CALL_POWER_NATIONAL
                && agent.getCallPower() != Const.AGENT_CALL_POWER_INTERNAL && agent.getCallPower() != Const.AGENT_CALL_POWER_LOCAL)) {
            if (isCreateAgent(agent)
                    || (!isCreateAgent(agent) && agent.getCallPower() == null)) {
                agent.setCallPower(Const.AGENT_CALL_POWER_ALL);
            }
        }
        if (agent.getWrapup() == null || agent.getWrapup() < 0) {
            agent.setWrapup(Const.AGENT_WRAPUP_DELFAULT);
        }

        if (agent.getIsOb() == null ||
                (agent.getIsOb() != Const.AGENT_IS_OB_DEFAULT && agent.getIsOb() != Const.AGENT_IS_OB_OFF)) {
            if (isCreateAgent(agent)
                    || (!isCreateAgent(agent) && agent.getIsOb() == null)) {
                agent.setIsOb(Const.AGENT_IS_OB_DEFAULT);
            }
        }
        if (agent.getIbRecord() == null ||
                (agent.getIbRecord() != Const.AGENT_IB_RECORD_DEFAULT && agent.getIbRecord() != Const.AGENT_IB_RECORD_OFF)) {
            if (isCreateAgent(agent)
                    || (!isCreateAgent(agent) && agent.getIbRecord() == null)) {
                agent.setIbRecord(Const.AGENT_IB_RECORD_DEFAULT);
            }
        }
        if (agent.getObRecord() == null ||
                (agent.getObRecord() != Const.AGENT_OB_RECORD_DEFAULT && agent.getObRecord() != Const.AGENT_OB_RECORD_OFF)) {
            if (isCreateAgent(agent)
                    || (!isCreateAgent(agent) && agent.getObRecord() == null)) {
                agent.setObRecord(Const.AGENT_OB_RECORD_DEFAULT);
            }
        }

        return null;
    }

    private boolean isCreateAgent(Agent agent) {
        if (agent.getId() == null || agent.getId() <= 0) {
            return true;
        }

        return false;
    }

    //cache
    public void setCache(Agent agent) {
        redisService.set(getKey(agent), agent);
    }

    public void deleteCache(Agent agent) {
        //agent
        redisService.delete(getKey(agent));
        //agent_tel
        redisService.delete(String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID_CNO, agent.getEnterpriseId(), agent.getCno()));
    }

    private String getKey(Agent agent) {
        return String.format(CacheKey.AGENT_ENTERPRISE_ID_CNO, agent.getEnterpriseId(), agent.getCno());
    }

    private void setRefreshCacheMethod(String methodName, Agent agent) {
        try {
            Method method = this.getClass().getMethod(methodName, Agent.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, agent);
            ProviderFilter.methodThreadLocal.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("AgentServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }

}
