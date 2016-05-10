package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.dao.QueueMemberDao;
import com.tinet.ctilink.conf.dao.QueueSkillDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.QueueMember;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.model.Queue;
import com.tinet.ctilink.conf.service.v1.CtiLinkQueueService;
import com.tinet.ctilink.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/4/7 17:17
 */
@Service
public class CtiLinkQueueServiceImp extends BaseService<Queue> implements CtiLinkQueueService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private QueueMemberDao queueMemberDao;

    @Autowired
    private QueueSkillDao queueSkillDao;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<Queue> createQueue(Queue queue) {
        //参数验证
        if (!entityDao.validateEntity(queue.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        //validate
        queue.setId(null);
        ApiResult<Queue> result = validateQueue(queue);
        if (result != null) {
            return result;
        }

        queue.setCreateTime(new Date());
        int count = insertSelective(queue);
        if (count != 1) {
            logger.error("CtiLinkQueueServiceImp.createQueue error, " + queue + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
        } else {
            setRefreshCacheMethod("setCache", queue);
            //TODO BigQueue 调用ami接口? 进行底层同步?
            return new ApiResult<>(queue);
        }
    }

    @Override
    public ApiResult deleteQueue(Queue queue) {
        //参数验证
        if (!entityDao.validateEntity(queue.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        //队列里面有在线座席, 不能删除


        //删除队列成员  queue_member
        Condition qmCondition = new Condition(Queue.class);
        Condition.Criteria qmCriteria = qmCondition.createCriteria();
        qmCriteria.andEqualTo("enterpriseId", queue.getEnterpriseId());
        qmCriteria.andEqualTo("queueId", queue.getId());
        queueMemberDao.deleteByCondition(qmCondition);
        //删除队列技能 queue_skill
        Condition qsCondition = new Condition(Queue.class);
        Condition.Criteria qsCriteria = qsCondition.createCriteria();
        qsCriteria.andEqualTo("enterpriseId", queue.getEnterpriseId());
        qsCriteria.andEqualTo("queueId", queue.getId());
        queueSkillDao.deleteByCondition(qsCondition);

        int count = deleteByPrimaryKey(queue.getId());

        //TODO BigQueue 调用ami接口? 进行底层同步?
        setRefreshCacheMethod("deleteCache", queue);
        return null;
    }

    @Override
    public ApiResult<Queue> updateQueue(Queue queue) {
        //参数验证
        if (!entityDao.validateEntity(queue.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        if (queue.getId() == null || queue.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        ApiResult<Queue> result = validateQueue(queue);
        if (result != null) {
            return result;
        }
        Queue dbQueue = selectByPrimaryKey(queue.getId());
        if (!queue.getEnterpriseId().equals(dbQueue.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]或[id]不正确");
        }

        queue.setQno(dbQueue.getQno());
        queue.setCreateTime(dbQueue.getCreateTime());
        int count = updateByPrimaryKeySelective(queue);

        if (count != 1) {
            logger.error("CtiLinkQueueServiceImp.updateQueue error, " + queue + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }
        setRefreshCacheMethod("setCache", queue);
        //TODO BigQueue 调用ami接口? 进行底层同步?
        return new ApiResult<>(queue);
    }

    @Override
    public ApiResult<List<Queue>> listQueue(Queue queue) {
        //参数验证
        if (!entityDao.validateEntity(queue.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        Condition condition = new Condition(Queue.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", queue.getEnterpriseId());
        condition.setOrderByClause("qno");
        List<Queue> list = selectByCondition(condition);
        return new ApiResult<>(list);
    }

    @Override
    public ApiResult<Queue> getQueue(Queue queue) {
        //参数验证
        if (!entityDao.validateEntity(queue.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (queue.getId() == null || queue.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        Queue dbQueue = selectByPrimaryKey(queue.getId());
        if (dbQueue == null || !queue.getEnterpriseId().equals(dbQueue.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]或[id]不正确");
        }

        return new ApiResult<>(dbQueue);
    }

    private <T> ApiResult<T> validateQueue(Queue queue) {
        if (StringUtils.isEmpty(queue.getDescription())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[description]不能为空");
        }

        if (queue.getDescription().length() > 25) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[description]长度不能超过25个字符");
        }

        if (queue.getMaxLen() == null || queue.getMaxLen() < 0
                || queue.getMaxLen() > 999) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[maxLen]格式不正确");
        }

        if (queue.getMemberTimeout() == null || queue.getMemberTimeout() < 20
                || queue.getMemberTimeout() > 60) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[memberTimeout]格式不正确");
        }

        if (queue.getQueueTimeout() == null || queue.getQueueTimeout() < 30
                || queue.getQueueTimeout() > 600) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[queueTimeout]格式不正确");
        }

        if (StringUtils.isEmpty(queue.getStrategy())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[strategy]不能为空");
        }

        if (!queue.getStrategy().equals(Const.STRATEGY_FEWEST_CALLS) &&
                !queue.getStrategy().equals(Const.STRATEGY_LEASTRECENT) &&
                !queue.getStrategy().equals(Const.STRATEGY_ORDER) &&
                !queue.getStrategy().equals(Const.STRATEGY_RANDOM) &&
                !queue.getStrategy().equals(Const.STRATEGY_RRMEMORY) &&
                !queue.getStrategy().equals(Const.STRATEGY_RRORDERED) &&
                !queue.getStrategy().equals(Const.STRATEGY_SKILL) &&
                !queue.getStrategy().equals(Const.STRATEGY_WRANDOM)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[strategy]不正确");
        }

        //验证music Class
        if (StringUtils.isEmpty(queue.getMusicClass())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[musicClass]不能为空");
        }

        if (queue.getSayAgentno() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[sayAgentno]不正确");
        }

        if (queue.getWrapupTime() == null || queue.getWrapupTime() < 3
                || queue.getWrapupTime() > 300) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[wrapupTime]格式不正确");
        }

        if (queue.getRetry() == null || queue.getRetry() < 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[retry]格式不正确");
        }

        if (queue.getWeight() == null || queue.getWeight() < 1
                || queue.getWeight() > 10) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[weight]格式不正确");
        }

        if (queue.getServiceLevel() == null || queue.getServiceLevel() < 1
                || queue.getServiceLevel() > 60) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[serviceLevel]格式不正确");
        }

        if (queue.getVipSupport() == null
                || (queue.getVipSupport() != Const.QUEUE_VIP_SUPPORT_DEFAULT
                && queue.getVipSupport() != Const.QUEUE_VIP_SUPPORT_YES)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[vipSupport]格式不正确");
        }

        if (queue.getId() == null) {  //create
            if (queue.getQno() == null) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[qno]不能为空");
            }
            if (!queue.getQno().startsWith(String.valueOf(queue.getEnterpriseId()))) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[qno]格式不正确");
            }
            if (!StringUtils.isNumeric(queue.getQno())
                    || (queue.getQno().length() - String.valueOf((queue.getEnterpriseId())).length() != 4)) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[qno]格式不正确");
            }
            //队列号已存在
            Condition condition = new Condition(Queue.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("enterpriseId", queue.getEnterpriseId());
            criteria.andEqualTo("qno", queue.getQno());
            List<Queue> list = selectByCondition(condition);
            if (list != null && list.size() > 0) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "队列号已经存在");
            }
        }

        return null;
    }

    public void setCache(Queue queue) {
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_ENTERPRISE_ID_QNO, queue.getEnterpriseId(), queue.getQno()), queue);
    }

    public void deleteCache(Queue queue) {
        //queue_memeber
        redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_QNO, queue.getEnterpriseId(), queue.getQno()));
        Set<String> cnoKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_QNO_CNO
                , queue.getEnterpriseId(), queue.getQno(), "*"));
        //删除座席
        for (String key : cnoKeySet) {
            QueueMember queueMember = redisService.get(Const.REDIS_DB_CONF_INDEX, key, QueueMember.class);
            String cno = key.substring(key.lastIndexOf("."), key.length());
            Condition condition = new Condition(QueueMember.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("enterpriseId", queueMember.getEnterpriseId());
            criteria.andEqualTo("agentId", queueMember.getAgentId());
            List<QueueMember> list = queueMemberDao.selectByCondition(condition);
            if (list != null &&list.size() > 0) {
                redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_CNO, queue.getEnterpriseId(), cno), list);
            } else {
                redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_CNO, queue.getEnterpriseId(), cno));
            }
        }
        //删除 cti-link.queue_member.{qno}.cno.{cno}=json
        redisService.delete(Const.REDIS_DB_CONF_INDEX, cnoKeySet);
        //queue
        redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.QUEUE_ENTERPRISE_ID_QNO, queue.getEnterpriseId(), queue.getQno()));
    }

    private void setRefreshCacheMethod(String methodName, Queue queue) {
        try {
            Method method = this.getClass().getMethod(methodName, Queue.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, queue);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("CtiLinkAgentServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }
}
