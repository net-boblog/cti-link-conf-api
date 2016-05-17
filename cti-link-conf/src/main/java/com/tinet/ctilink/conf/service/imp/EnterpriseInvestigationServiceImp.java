package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.dao.EnterpriseInvestigationDao;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseInvestigation;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseInvestigationService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.util.SqlUtil;
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
 * @date 16/4/27 14:00
 */
@Service
public class EnterpriseInvestigationServiceImp extends BaseService<EnterpriseInvestigation> implements CtiLinkEnterpriseInvestigationService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private EnterpriseInvestigationDao enterpriseInvestigationDao;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<EnterpriseInvestigation> createEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseInvestigation.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        enterpriseInvestigation.setId(null);
        if (StringUtils.isEmpty(enterpriseInvestigation.getPathName())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[pathName]不能为空");
        }
        enterpriseInvestigation.setPathName(SqlUtil.escapeSql(enterpriseInvestigation.getPathName()));

        if (StringUtils.isEmpty(enterpriseInvestigation.getPath())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[path]不能为空");
        }
        enterpriseInvestigation.setPath(SqlUtil.escapeSql(enterpriseInvestigation.getPath()));
        //验证path?

        if (enterpriseInvestigation.getAction() == null
                || (enterpriseInvestigation.getAction() != Const.ENTERPRISE_IVR_OP_ACTION_PLAY
                && enterpriseInvestigation.getAction() != Const.ENTERPRISE_IVR_OP_ACTION_SELECT)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[action]不正确");
        }

        if (StringUtils.isEmpty(enterpriseInvestigation.getProperty())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[property]不能为空");
        }
        enterpriseInvestigation.setProperty(SqlUtil.escapeSql(enterpriseInvestigation.getProperty()));
        //验证property?

        if (enterpriseInvestigation.getParentId() == null
                || enterpriseInvestigation.getParentId() < 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[parentId]不能为空");
        }

        //判断根节点是否存在
        if (enterpriseInvestigation.getParentId() == 0) {
            if (!enterpriseInvestigation.getPath().equals("1")) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "根节点的path必须等于1");
            }
        }
        enterpriseInvestigation.setAnchor(SqlUtil.escapeSql(enterpriseInvestigation.getAnchor()));

        //判断path是否合法
        ApiResult<EnterpriseInvestigation> result = validatePath(enterpriseInvestigation.getEnterpriseId(),
                enterpriseInvestigation.getParentId(), enterpriseInvestigation.getPath());
        if (result != null) {
            return result;
        }
        enterpriseInvestigation.setCreateTime(new Date());
        int count = insertSelective(enterpriseInvestigation);
        if (count != 1) {
            logger.error("EnterpriseInvestigationServiceImp.createEnterpriseInvestigation error, " + enterpriseInvestigation + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
        } else {
            setRefreshCacheMethod(enterpriseInvestigation.getEnterpriseId());
            return new ApiResult<>(enterpriseInvestigation);
        }
    }

    @Override
    public ApiResult deleteEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation) {
        //删除一条要把子节点也删除了
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseInvestigation.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        if (enterpriseInvestigation.getId() == null
                || enterpriseInvestigation.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        EnterpriseInvestigation dbEnterpriseInvestigation = selectByPrimaryKey(enterpriseInvestigation.getId());

        if (dbEnterpriseInvestigation == null
                || !enterpriseInvestigation.getEnterpriseId().equals(dbEnterpriseInvestigation.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        int count = enterpriseInvestigationDao.deleteEnterpriseInvestigation(enterpriseInvestigation.getId());

        if (count <= 0) {
            logger.error("EnterpriseInvestigationServiceImp.deleteEnterpriseInvestigation error, " + enterpriseInvestigation + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "删除失败");
        }
        setRefreshCacheMethod(dbEnterpriseInvestigation.getEnterpriseId());
        return new ApiResult<>(ApiResult.SUCCESS_RESULT);
    }

    @Override
    public ApiResult<EnterpriseInvestigation> updateEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseInvestigation.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (enterpriseInvestigation.getId() == null
                || enterpriseInvestigation.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        if (StringUtils.isEmpty(enterpriseInvestigation.getPathName())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[pathName]不能为空");
        }
        enterpriseInvestigation.setPathName(SqlUtil.escapeSql(enterpriseInvestigation.getPathName()));

        if (StringUtils.isEmpty(enterpriseInvestigation.getProperty())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[property]不能为空");
        }
        enterpriseInvestigation.setProperty(SqlUtil.escapeSql(enterpriseInvestigation.getProperty()));
        //验证property?
        enterpriseInvestigation.setAnchor(SqlUtil.escapeSql(enterpriseInvestigation.getAnchor()));

        EnterpriseInvestigation dbEnterpriseInvestigation = selectByPrimaryKey(enterpriseInvestigation.getId());
        if (dbEnterpriseInvestigation == null
                || !enterpriseInvestigation.getEnterpriseId().equals(dbEnterpriseInvestigation.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        dbEnterpriseInvestigation.setPathName(enterpriseInvestigation.getPathName());
        dbEnterpriseInvestigation.setProperty(enterpriseInvestigation.getProperty());
        dbEnterpriseInvestigation.setAnchor(enterpriseInvestigation.getAnchor());

        int count = updateByPrimaryKeySelective(dbEnterpriseInvestigation);
        if (count != 1) {
            logger.error("EnterpriseInvestigationServiceImp.updateEnterpriseInvestigation error, " + dbEnterpriseInvestigation + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }
        setRefreshCacheMethod(dbEnterpriseInvestigation.getEnterpriseId());
        return new ApiResult<>(dbEnterpriseInvestigation);
    }

    @Override
    public ApiResult<List<EnterpriseInvestigation>> listEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseInvestigation.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        Condition condition = new Condition(EnterpriseInvestigation.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseInvestigation.getEnterpriseId());
        condition.setOrderByClause("path");
        List<EnterpriseInvestigation> list = selectByCondition(condition);

        return new ApiResult<>(list);
    }

    @Override
    public ApiResult<EnterpriseInvestigation> getEnterpriseInvestigation(EnterpriseInvestigation enterpriseInvestigation) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseInvestigation.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        if (enterpriseInvestigation.getId() == null
                || enterpriseInvestigation.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        EnterpriseInvestigation dbEnterpriseInvestigation = selectByPrimaryKey(enterpriseInvestigation.getId());
        if (dbEnterpriseInvestigation == null
                || !enterpriseInvestigation.getEnterpriseId().equals(dbEnterpriseInvestigation.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        return new ApiResult<>(dbEnterpriseInvestigation);
    }

    private ApiResult<EnterpriseInvestigation> validatePath(Integer enterpriseId, Integer parentId, String path) {

        if (parentId != 0) {
            EnterpriseInvestigation enterpriseInvestigation  = selectByPrimaryKey(parentId);
            if (enterpriseInvestigation == null) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "父节点不存在");
            }
            if (!path.startsWith(enterpriseInvestigation.getPath() + ".")) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[path]不正确");

            }
            if (!StringUtils.isNumeric(path.substring(enterpriseInvestigation.getPath().length() + 1, path.length()))) {
                return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[path]不正确");
            }
        }

        Condition condition = new Condition(EnterpriseInvestigation.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        criteria.andEqualTo("parentId", parentId);
        criteria.andEqualTo("path", path);
        int count = selectCountByCondition(condition);
        if (count > 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "节点已存在");
        }
        return null;
    }

    public void refreshCache(Integer enterpriseId) {
        Condition condition = new Condition(EnterpriseInvestigation.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("path");
        List<EnterpriseInvestigation> list = selectByCondition(condition);
        //满意度调查, 每个企业一个key, 直接重新设置
        if (list == null || list.isEmpty()) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_INVESTIGATION_ENTERPRISE_ID, enterpriseId));
        } else {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_INVESTIGATION_ENTERPRISE_ID, enterpriseId), list);
        }
    }

    private void setRefreshCacheMethod(Integer enterpriseId) {
        try {
            Method method = this.getClass().getMethod("refreshCache", Integer.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, enterpriseId);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("EnterpriseInvestigationServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }
}
