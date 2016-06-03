package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.mapper.AgentSkillMapper;
import com.tinet.ctilink.conf.mapper.AgentTelMapper;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.service.QueueMemberService;
import com.tinet.ctilink.conf.service.v1.CtiLinkAgentService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.model.Agent;
import com.tinet.ctilink.conf.model.AgentSkill;
import com.tinet.ctilink.conf.model.AgentTel;
import com.tinet.ctilink.conf.model.QueueMember;
import com.tinet.ctilink.conf.request.AgentListRequest;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author fengwei //
 * @date 16/4/7 16:49
 */
@Service
public class AgentServiceImp extends BaseService<Agent> implements CtiLinkAgentService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private AgentSkillMapper agentSkillMapper;

    @Autowired
    private AgentTelMapper agentTelMapper;

    @Autowired
    private QueueMemberService queueMemberService;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<Agent> createAgent(Agent agent) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(agent.getEnterpriseId())) {
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

    //批量新增座席
    @Override
    public ApiResult batchCreateAgent(List<Agent> agentList) {
        return null;
    }

    @Override
    public ApiResult deleteAgent(Agent agent) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(agent.getEnterpriseId())) {
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
        queueMemberService.deleteByCondition(qmCondition);

        Condition atCondition = new Condition(AgentTel.class);
        Condition.Criteria atCriteria = atCondition.createCriteria();
        atCriteria.andEqualTo("agentId", agent.getId());
        atCondition.setTableName("cti_link_agent_tel");
        agentTelMapper.deleteByCondition(atCondition);

        Condition asCondition = new Condition(AgentSkill.class);
        Condition.Criteria asCriteria = asCondition.createCriteria();
        asCriteria.andEqualTo("agentId", agent.getId());
        asCondition.setTableName("cti_link_agent_skill");
        agentSkillMapper.deleteByCondition(asCondition);

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
        if (!entityMapper.validateEntity(agent.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agent.getId() == null || agent.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        //判断在不在线?

        //校验参数
        ApiResult<Agent> result = validateAgent(agent);
        if (result != null) {
            return result;
        }

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
    public ApiResult<PageInfo<Agent>> listAgent(AgentListRequest request) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(request.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (request.getLimit() <= 0 || request.getLimit() > 500) {
            request.setLimit(10);
        }
        if (request.getOffset() <= 0) {
            request.setOffset(0);
        }

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
        Page<Agent> page = PageHelper.startPage(request.getOffset() / request.getLimit() + 1, request.getLimit());

        selectByCondition(condition);
        PageInfo<Agent> pageInfo = page.toPageInfo();
        return new ApiResult<>(pageInfo);
    }

    @Override
    public ApiResult<Agent> getAgent(Agent agent) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(agent.getEnterpriseId())) {
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
        return (agent.getId() == null || agent.getId() <= 0);
    }


    /**
     * 座席上线,修改绑定电话,添加入对应队列
     */
    @Override
    public String updateAgentOnline(Integer enterpriseId, String cno, String bindTel, Integer bindType) {
        Condition condition = new Condition(Agent.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        criteria.andEqualTo("cno", cno);
        List<Agent> agentList = selectByCondition(condition);
        Agent agent = agentList.get(0);

        String oldBindTel = "";
        AgentTel agentTel = agentTelMapper.getBindTel(agent.getId());
        if (agentTel != null) {
            oldBindTel = agentTel.getTel();
        }

        int telType;
        if (bindType == Const.BIND_TYPE_EXTEN) {  //分机, 软电话
            // 分机或软电话格式校验
            if (!Pattern.compile(Const.EXTEN_TEL_VALIDATION).matcher(bindTel).find()) {
                return "invalid tel format";
            }
            telType = Const.TEL_TYPE_EXTEN;
        } else if (bindType == Const.BIND_TYPE_TEL) {  //电话号码
            // 判断号码格式
            if (Pattern.compile(Const.LANDLINE_VALIDATION).matcher(bindTel).find()) {
                telType = Const.TEL_TYPE_LANDLINE;
            } else if (Pattern.compile(Const.MOBILE_VALIDATION).matcher(bindTel).find()) {
                telType = Const.TEL_TYPE_MOBILE;
            } else {
                return "invalid tel format";
            }
        } else {
            return "invalid bindType";
        }

        //判断号码是否存在且被其他座席绑定
        //查询所有有bindTel的
        Condition condition1 = new Condition(AgentTel.class);
        Condition.Criteria criteria1 = condition1.createCriteria();
        criteria1.andEqualTo("enterpriseId", enterpriseId);
        criteria1.andEqualTo("tel", bindTel);
        List<AgentTel> agentTelList = agentTelMapper.selectByCondition(condition1);
        AgentTel bindAgentTel = null;  //座席是否已经绑定了或者已经有这个号码
        if (agentTelList != null && !agentTelList.isEmpty()) {
            //是否有座席绑定了bindTel
            for (AgentTel agentTel1 : agentTelList) {
                if (agentTel1.getIsBind() == Const.AGENT_TEL_IS_BIND_YES) {
                    if (!agentTel1.getAgentId().equals(agent.getId())) {
                        return "bindTel already bind by " + agentTel1.getCno();
                    } else {
                        bindAgentTel = agentTel1;
                        break;
                    }
                } else {
                    if (agentTel1.getAgentId().equals(agent.getId())) {
                        bindAgentTel = agentTel1;
                    }
                }
            }
        }

        //没找到bindAgentTel
        if (bindAgentTel == null) {
            bindAgentTel = new AgentTel();
            bindAgentTel.setEnterpriseId(enterpriseId);
            bindAgentTel.setAgentId(agent.getId());
            bindAgentTel.setCno(agent.getCno());
            bindAgentTel.setTel(bindTel);
            bindAgentTel.setTelType(telType);
        }
        bindAgentTel.setIsBind(Const.AGENT_TEL_IS_BIND_YES);

        //先保存
        if (bindAgentTel.getId() == null) {
            agentTelMapper.insertSelective(bindAgentTel);
        }
        //更新座席电话绑定情况
        agentTelMapper.updateBind(bindAgentTel);

        //更新queueMember
        if (!bindTel.equals(oldBindTel) || bindType == Const.BIND_TYPE_EXTEN) {
            queueMemberService.updateByAgent(agent, bindTel, telType);
        }

        //删除之前绑定的分机或软电话
        Condition condition2 = new Condition(AgentTel.class);
        Condition.Criteria criteria2 = condition2.createCriteria();
        criteria2.andEqualTo("enterpriseId", enterpriseId);
        criteria2.andEqualTo("agentId",  agent.getId());
        criteria2.andEqualTo("isBind",  Const.AGENT_TEL_IS_BIND_NO);
        criteria2.andEqualTo("telType",  Const.TEL_TYPE_EXTEN);
        agentTelMapper.deleteByCondition(condition2);

        setRefreshCacheMethod("updateCache", agent);
        return "success";
    }

    //cache
    public void setCache(Agent agent) {
        redisService.set(Const.REDIS_DB_CONF_INDEX, getKey(agent), agent);
    }

    //缓存
    public void updateCache(Agent agent) {
        //agent_tel
        //座席电话，第一个是绑定的电话
        Condition condition = new Condition(AgentTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", agent.getEnterpriseId());
        criteria.andEqualTo("agentId", agent.getId());
        condition.setOrderByClause("is_bind desc, id");
        List<AgentTel> list = agentTelMapper.selectByCondition(condition);
        if (list != null && list.size() > 0) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID_CNO, agent.getEnterpriseId(), agent.getCno())
                    , list);
        } else {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID_CNO, agent.getEnterpriseId(), agent.getCno()));
        }

        updateQueueMemeberCache(agent);
    }

    public void deleteCache(Agent agent) {
        //agent
        redisService.delete(Const.REDIS_DB_CONF_INDEX, getKey(agent));
        //agent_tel
        redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID_CNO, agent.getEnterpriseId(), agent.getCno()));

        updateQueueMemeberCache(agent);
    }

    private void updateQueueMemeberCache(Agent agent) {
        Condition condition1 = new Condition(QueueMember.class);
        Condition.Criteria criteria1 = condition1.createCriteria();
        criteria1.andEqualTo("enterpriseId", agent.getEnterpriseId());
        criteria1.andEqualTo("cno", agent.getCno());
        List<QueueMember> queueMemberList = queueMemberService.selectByCondition(condition1);

        Set<Integer> queueIdSet = new HashSet<>();
        Set<String> qnoCnoDbKeySet = new HashSet<>();
        if (queueMemberList != null && queueMemberList.size() > 0) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_CNO
                    , agent.getEnterpriseId(), agent.getCno()), queueMemberList);
            for (QueueMember queueMember : queueMemberList) {
                String key = String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_QNO_CNO, agent.getEnterpriseId(), queueMember.getQno(), queueMember.getCno());
                redisService.set(Const.REDIS_DB_CONF_INDEX, key, queueMember);
                qnoCnoDbKeySet.add(key);
                queueIdSet.add(queueMember.getQueueId());
            }
        } else {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_CNO
                    , agent.getEnterpriseId(), agent.getCno()));
        }

        Set<String> qnoCnoExistKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_QNO_CNO, agent.getEnterpriseId(), "*", agent.getCno()));
        qnoCnoExistKeySet.removeAll(qnoCnoDbKeySet);
        if (qnoCnoExistKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, qnoCnoExistKeySet);
        }

        if (queueIdSet.size() > 0) {
            Iterator<Integer> iterator = queueIdSet.iterator();
            while (iterator.hasNext()) {
                int queueId = iterator.next();
                Condition condition2 = new Condition(QueueMember.class);
                Condition.Criteria criteria2 = condition2.createCriteria();
                criteria2.andEqualTo("enterpriseId", agent.getEnterpriseId());
                criteria2.andEqualTo("queueId", queueId);
                List<QueueMember> list = queueMemberService.selectByCondition(condition2);
                if (list != null &&list.size() > 0) {
                    redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_QNO, agent.getEnterpriseId(), queueId), list);
                } else {
                    redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_QNO, agent.getEnterpriseId(), queueId));
                }
            }
        }

    }

    private String getKey(Agent agent) {
        return String.format(CacheKey.AGENT_ENTERPRISE_ID_CNO, agent.getEnterpriseId(), agent.getCno());
    }

    private void setRefreshCacheMethod(String methodName, Agent agent) {
        try {
            Method method = this.getClass().getMethod(methodName, Agent.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, agent);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("AgentServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }

}
