package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.model.QueueSkill;
import com.tinet.ctilink.conf.service.v1.CtiLinkQueueSkillService;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/21 13:24
 */
@Service
public class QueueSkillServiceImp extends BaseService<QueueSkill> implements CtiLinkQueueSkillService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    EntityMapper entityMapper;


    @Override
    public ApiResult<QueueSkill> createQueueSkill(QueueSkill queueSkill) {
        //参数验证
        if (!entityMapper.validateEntity(queueSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        return null;
    }

    @Override
    public ApiResult<QueueSkill> deleteQueueSkill(QueueSkill queueSkill) {
        //参数验证
        if (!entityMapper.validateEntity(queueSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (queueSkill.getId() != null || queueSkill.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        //TODO 有座席在线能删除吗?



        deleteByPrimaryKey(queueSkill.getId());
        return null;
    }

    @Override
    public ApiResult<QueueSkill> updateQueueSkill(QueueSkill queueSkill) {
        //参数验证
        if (!entityMapper.validateEntity(queueSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (queueSkill.getId() != null || queueSkill.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        //TODO 有座席在线能删除吗?

        QueueSkill dbQueueSkill = selectByPrimaryKey(queueSkill.getId());
        if (dbQueueSkill == null || !queueSkill.getEnterpriseId().equals(dbQueueSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        //只有技能值可以更新
        dbQueueSkill.setSkillLevel(queueSkill.getSkillLevel());
        int count = updateByPrimaryKey(dbQueueSkill);
        if (count != 1) {
            logger.error("QueueSkillServiceImp.updateQueueSkill error, " + dbQueueSkill + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }

        return new ApiResult<>(dbQueueSkill);
    }

    @Override
    public ApiResult<List<QueueSkill>> listQueueSkill(QueueSkill queueSkill) {
        //参数验证
        if (!entityMapper.validateEntity(queueSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (queueSkill.getQueueId() == null || queueSkill.getQueueId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[queueId]不能为空");
        }

        Condition condition = new Condition(QueueSkill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", queueSkill.getEnterpriseId());
        criteria.andEqualTo("queueId", queueSkill.getQueueId());
        condition.setOrderByClause("id");
        List<QueueSkill> list = selectByCondition(condition);

        return new ApiResult<>(list);
    }

}
