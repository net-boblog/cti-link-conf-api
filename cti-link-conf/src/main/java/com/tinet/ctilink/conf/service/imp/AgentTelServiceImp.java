package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.dao.AgentDao;
import com.tinet.ctilink.conf.dao.AgentTelDao;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.model.Agent;
import com.tinet.ctilink.conf.model.AgentTel;
import com.tinet.ctilink.conf.service.v1.AgentTelService;
import com.tinet.ctilink.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/19 17:44
 */
@Service
public class AgentTelServiceImp extends BaseService<AgentTel> implements AgentTelService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private AgentTelDao agentTelDao;

    @Autowired
    private AgentDao agentDao;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<AgentTel> createAgentTel(AgentTel agentTel) {
        //如果新增的是绑定电话(isBind=1)，需要判断座席是否在线，在线的话，不能新增绑定电话，如果不在线，需要把其他绑定电话设置为isBind=0
        if (!entityDao.validateEntity(agentTel.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        agentTel.setId(null);
        ApiResult<AgentTel> result = validateAgentTel(agentTel);
        if (result != null) {
            return result;
        }

        agentTel.setCreateTime(new Date());
        int count = insertSelective(agentTel);
        if (count != 1) {
            logger.error("AgentTelServiceImp.createAgentTel error, " + agentTel + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
        }

        //TODO 绑定电话, 查询座席是否在线
        if (agentTel.getIsBind() == Const.AGENT_TEL_IS_BIND_YES) {

            //将座席的其他号码全部设置为未绑定
            boolean r = agentTelDao.updateAgentTelBind(agentTel.getAgentId(), agentTel.getTel());

            //更新queue_memeber里面的tel ?
        }

        //如果不是绑定电话, 只新增就可以
        setRefreshCacheMethod(agentTel);
        return new ApiResult<>(agentTel);
    }

    @Override
    public ApiResult deleteAgentTel(AgentTel agentTel) {
        //删除前, 需要先解绑
        if (!entityDao.validateEntity(agentTel.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agentTel.getId() == null || agentTel.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        AgentTel dbAgentTel = selectByPrimaryKey(agentTel.getId());
        if (dbAgentTel == null || !agentTel.getEnterpriseId().equals(dbAgentTel.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        if (dbAgentTel.getIsBind() == Const.AGENT_TEL_IS_BIND_YES) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "电话正在使用中，请先解绑");
        }

        int count = deleteByPrimaryKey(agentTel.getId());
        if (count != 1) {
            logger.error("AgentTelServiceImp.deleteAgentTel error, " + agentTel + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "删除失败");
        }

        setRefreshCacheMethod(agentTel);
        return new ApiResult(ApiResult.SUCCESS_RESULT);
    }

    @Override
    public ApiResult<AgentTel> updateAgentTel(AgentTel agentTel) {
        //绑定, 解绑. 解绑前, 需要下下线
        if (!entityDao.validateEntity(agentTel.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agentTel.getId() == null || agentTel.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        ApiResult<AgentTel> result = validateAgentTel(agentTel);
        if (result != null) {
            return result;
        }

        AgentTel dbAgentTel = selectByPrimaryKey(agentTel.getId());
        if (dbAgentTel == null || !agentTel.getEnterpriseId().equals(dbAgentTel.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        //TODO 判断座席是否在线, 在线情况下不能修改

        //如果不在线
        if (agentTel.getIsBind() == Const.AGENT_TEL_IS_BIND_YES) {
            if (dbAgentTel.getIsBind() == Const.AGENT_TEL_IS_BIND_YES) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败，电话已经绑定");
            }
            dbAgentTel.setIsBind(Const.AGENT_TEL_IS_BIND_YES);
            updateByPrimaryKey(dbAgentTel);

            agentTelDao.updateAgentTelBind(agentTel.getAgentId(), agentTel.getTel());

            //queue_memeber?
        } else {
            if (dbAgentTel.getIsBind() == Const.AGENT_TEL_IS_BIND_NO) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败，电话已经解绑");
            }
            dbAgentTel.setIsBind(Const.AGENT_TEL_IS_BIND_NO);
            updateByPrimaryKey(dbAgentTel);
        }
        setRefreshCacheMethod(agentTel);

        return null;
    }

    @Override
    public ApiResult<List<AgentTel>> listAgentTel(AgentTel agentTel) {
        if (!entityDao.validateEntity(agentTel.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (agentTel.getAgentId() == null || agentTel.getAgentId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[agentId]不能为空");
        }

        Condition condition = new Condition(AgentTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", agentTel.getEnterpriseId());
        criteria.andEqualTo("agentId", agentTel.getAgentId());
        condition.setOrderByClause("is_bind desc, id");
        List<AgentTel> list = selectByCondition(condition);

        return new ApiResult<>(list);
    }

    private <T> ApiResult<T> validateAgentTel(AgentTel agentTel) {
        if (agentTel.getAgentId() == null || agentTel.getAgentId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[agentId]不正确");
        }

        agentTel.setIsValidity(Const.AGENT_TEL_IS_VALIDITY_DEFAULT);
        if (agentTel.getIsBind() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[isBind]不能为空");
        }
        if (agentTel.getIsBind() != Const.AGENT_TEL_IS_BIND_NO && agentTel.getIsBind() != Const.AGENT_TEL_IS_BIND_YES) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[isBind]不正确");
        }

        if (agentTel.getId() == null) {  //新增电话
            if (StringUtils.isEmpty(agentTel.getTel())) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[tel]不能为空");
            }
            if (agentTel.getTel().matches(Const.LANDLINE_VALIDATION)) {
                agentTel.setTelType(Const.TEL_TYPE_LANDLINE);
            } else if (agentTel.getTel().matches(Const.MOBILE_VALIDATION)) {
                agentTel.setTelType(Const.TEL_TYPE_MOBILE);
            } else {
                //TODO 分机和软电话
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[tel]格式不正确");
            }

            //判断号码是否已经被其他座席绑定或者自己已经添加了这个号码
            if (isAgentTelBinding(agentTel.getEnterpriseId(), agentTel.getTel())) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "操作失败，电话号码已经绑定");
            }

            if (isAgentTelCreated(agentTel.getEnterpriseId(), agentTel.getAgentId(), agentTel.getTel())) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "操作失败，座席已经添加了此号码");
            }
        }
        return null;
    }

    /**
     * 号码是否被其他座席绑定
     * @param enterpriseId
     * @param tel
     * @return
     */
    private boolean isAgentTelBinding(Integer enterpriseId, String tel) {
        Condition condition = new Condition(AgentTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        criteria.andEqualTo("tel", tel);
        criteria.andEqualTo("isBind", Const.AGENT_TEL_IS_BIND_YES);
        int count = selectCountByCondition(condition);
        if (count == 0) {
            return false;
        }
        return true;
    }

    /**
     * 座席是否已经添加了此号码
     * @param enterpriseId
     * @param agentId
     * @param tel
     * @return
     */
    private boolean isAgentTelCreated(Integer enterpriseId, Integer agentId, String tel) {
        Condition condition = new Condition(AgentTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        criteria.andEqualTo("tel", tel);
        criteria.andEqualTo("agentId", agentId);
        int count = selectCountByCondition(condition);
        if (count == 0) {
            return false;
        }
        return true;
    }

    //cache
    public void setCache(AgentTel agentTel) {
        //座席
        Agent agent = agentDao.selectByPrimaryKey(agentTel.getAgentId());
        //座席电话，第一个是绑定的电话
        Condition condition = new Condition(AgentTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", agentTel.getEnterpriseId());
        criteria.andEqualTo("agentId", agentTel.getAgentId());
        condition.setOrderByClause("is_bind desc, id");
        List<AgentTel> list = selectByCondition(condition);
        if (list != null && list.size() > 0) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID_CNO, agentTel.getEnterpriseId(), agent.getCno())
                    , list);
        } else {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID_CNO, agentTel.getEnterpriseId(), agent.getCno()));
        }
    }

    private void setRefreshCacheMethod(AgentTel agentTel) {
        try {
            Method method = this.getClass().getMethod("setCache", AgentTel.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, agentTel);
            ProviderFilter.methodThreadLocal.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("AgentServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }
}
