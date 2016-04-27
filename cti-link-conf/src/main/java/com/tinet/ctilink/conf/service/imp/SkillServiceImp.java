package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.conf.dao.AgentSkillDao;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.dao.QueueSkillDao;
import com.tinet.ctilink.conf.model.AgentSkill;
import com.tinet.ctilink.conf.model.QueueSkill;
import com.tinet.ctilink.conf.model.Skill;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.conf.service.v1.SkillService;
import com.tinet.ctilink.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:17
 */
@Service
public class SkillServiceImp extends BaseService<Skill> implements SkillService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private QueueSkillDao queueSkillDao;

    @Autowired
    private AgentSkillDao agentSkillDao;

    @Override
    public ApiResult<Skill> createSkill(Skill skill) {
        //参数验证
        if (!entityDao.validateEntity(skill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        ApiResult<Skill> result = validateSkill(skill);
        if (result != null) {
            return result;
        }
        //设置创建时间
        skill.setCreateTime(new Date());
        //插入
        int count = insertSelective(skill);

        if (count != 1) {  //新增失败
            logger.error("SkillServiceImp.createSkill error, " + skill + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
        } else {
            return new ApiResult<>(skill);
        }

    }

    @Override
    public ApiResult deleteSkill(Skill skill) {
        if (!entityDao.validateEntity(skill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        //
        if (skill.getId() == null || skill.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        //技能是否在使用中, agent_skill, queue_skill
        if (isSkillInUse(skill.getId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "删除失败，技能使用中");
        }

        Condition condition = new Condition(Skill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", skill.getEnterpriseId());
        criteria.andEqualTo("id", skill.getId());
        int count = deleteByCondition(condition);

        if (count != 1) {
            logger.error("SkillServiceImp.deleteSkill error, " + skill + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "删除失败");
        } else {
            return new ApiResult(ApiResult.SUCCESS_RESULT);
        }
    }

    @Override
    public ApiResult<Skill> updateSkill(Skill skill) {
        if (!entityDao.validateEntity(skill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (skill.getId() == null || skill.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        ApiResult<Skill> result = validateSkill(skill);
        if (result != null) {
            return result;
        }

        //判断id所在的enterprise是否和enterpriseId一致
        Skill dbSkill = selectByPrimaryKey(skill.getId());
        if (dbSkill == null || !skill.getEnterpriseId().equals(dbSkill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }
        skill.setCreateTime(dbSkill.getCreateTime());
        int count = updateByPrimaryKey(skill);

        if (count != 1) {
            logger.error("SkillServiceImp.updateSkill error, " + skill + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        } else {
            return new ApiResult<>(skill);
        }
    }

    @Override
    public ApiResult<List<Skill>> listSkill(Skill skill) {
        //校验enterpriseId
        if (!entityDao.validateEntity(skill.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        Condition condition = new Condition(Skill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", skill.getEnterpriseId());
        condition.setOrderByClause("id asc");
        List<Skill> list = selectByCondition(condition);

        return new ApiResult<>(list);
    }

    /**
     * 参数校验
     * @param skill
     * @param <T>
     * @return
     */
    private<T> ApiResult<T> validateSkill(Skill skill) {
        if (StringUtils.isEmpty(skill.getName())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[name]不能为空");
        }

        //过滤name和comment
        skill.setName(SqlUtil.escapeSql(skill.getName()));
        skill.setComment(SqlUtil.escapeSql(skill.getComment()));
        //判断name是否已经存在
        Condition condition = new Condition(Skill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", skill.getEnterpriseId());
        criteria.andEqualTo("name", skill.getName());
        List<Skill> list = selectByCondition(condition);
        if (list != null && list.size() > 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "技能名称已经存在");
        }

        return null;
    }

    /**
     * 技能是否在使用中
     * @param skillId
     * @return
     */
    private boolean isSkillInUse(int skillId) {
        //技能是否在使用中, agent_skill, queue_skill
        Condition condition = new Condition(AgentSkill.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("skillId", skillId);
        int count = agentSkillDao.selectCountByCondition(condition);
        if (count > 0) {
            return true;
        }

        //queue_skill
        condition = new Condition(QueueSkill.class);
        criteria = condition.createCriteria();
        criteria.andEqualTo("skillId", skillId);
        count = queueSkillDao.selectCountByCondition(condition);
        if (count > 0) {
            return true;
        }

        return false;
    }

}
