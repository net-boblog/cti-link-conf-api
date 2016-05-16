package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.dao.EnterpriseIvrDao;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.dao.IvrProfileDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseIvr;
import com.tinet.ctilink.conf.model.IvrProfile;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseIvrService;
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
 * @date 16/4/22 13:43
 */
@Service
public class CtiLinkEnterpriseIvrServiceImp extends BaseService<EnterpriseIvr> implements CtiLinkEnterpriseIvrService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private EnterpriseIvrDao enterpriseIvrDao;

    @Autowired
    private IvrProfileDao ivrProfileDao;

    @Autowired
    private RedisService redisService;

    @Override
    public CtiLinkApiResult<EnterpriseIvr> createEnterpriseIvr(EnterpriseIvr enterpriseIvr) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseIvr.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        CtiLinkApiResult<EnterpriseIvr> result = validateEnterpriseIvr(enterpriseIvr);
        if (result != null) {
            return result;
        }
        enterpriseIvr.setCreateTime(new Date());
        int count = insertSelective(enterpriseIvr);
        if (count != 1) {
            logger.error("CtiLinkEnterpriseIvrServiceImp.createEnterpriseIvr error, " + enterpriseIvr + ", count=" + count);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "新增失败");
        } else {
            setRefreshCacheMethod(enterpriseIvr);
            return new CtiLinkApiResult<>(enterpriseIvr);
        }
    }

    @Override
    public CtiLinkApiResult deleteEnterpriseIvr(EnterpriseIvr enterpriseIvr) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseIvr.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (enterpriseIvr.getId() == null
                || enterpriseIvr.getId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        EnterpriseIvr dbEnterpriseIvr = selectByPrimaryKey(enterpriseIvr.getId());

        if (dbEnterpriseIvr == null
                || !enterpriseIvr.getEnterpriseId().equals(dbEnterpriseIvr.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        int count = enterpriseIvrDao.deleteEnterpriseIvr(enterpriseIvr.getId());

        if (count <= 0) {
            logger.error("CtiLinkEnterpriseIvrServiceImp.deleteEnterpriseIvr error, " + enterpriseIvr + ", count=" + count);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "删除失败");
        }
        setRefreshCacheMethod(dbEnterpriseIvr);
        return new CtiLinkApiResult<>(CtiLinkApiResult.SUCCESS_RESULT);
    }

    @Override
    public CtiLinkApiResult<EnterpriseIvr> updateEnterpriseIvr(EnterpriseIvr enterpriseIvr) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseIvr.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        CtiLinkApiResult<EnterpriseIvr> result = validateEnterpriseIvr(enterpriseIvr);
        if (result != null) {
            return result;
        }

        EnterpriseIvr dbEnterpriseIvr = selectByPrimaryKey(enterpriseIvr.getId());
        if (dbEnterpriseIvr == null || !enterpriseIvr.getEnterpriseId().equals(dbEnterpriseIvr.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        dbEnterpriseIvr.setPathName(enterpriseIvr.getPathName());
        dbEnterpriseIvr.setProperty(enterpriseIvr.getProperty());
        dbEnterpriseIvr.setAnchor(enterpriseIvr.getAnchor());
        int count = updateByPrimaryKeySelective(dbEnterpriseIvr);

        if (count != 1) {
            logger.error("CtiLinkEnterpriseIvrServiceImp.updateEnterpriseIvr error, " + dbEnterpriseIvr + ", count=" + count);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "更新失败");
        }
        setRefreshCacheMethod(dbEnterpriseIvr);
        return new CtiLinkApiResult<>(dbEnterpriseIvr);
    }

    @Override
    public CtiLinkApiResult<List<EnterpriseIvr>> listEnterpriseIvr(EnterpriseIvr enterpriseIvr) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseIvr.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (enterpriseIvr.getIvrId() == null || enterpriseIvr.getIvrId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[ivrId]不正确");
        }
        Condition condition = new Condition(EnterpriseIvr.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseIvr.getEnterpriseId());
        criteria.andEqualTo("ivrId", enterpriseIvr.getIvrId());
        condition.setOrderByClause("path");
        List<EnterpriseIvr> list = selectByCondition(condition);
        return new CtiLinkApiResult<>(list);
    }

    @Override
    public CtiLinkApiResult<EnterpriseIvr> getEnterpriseIvr(EnterpriseIvr enterpriseIvr) {
        //验证enterpriseId
        if (!entityDao.validateEntity(enterpriseIvr.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (enterpriseIvr.getId() == null || enterpriseIvr.getId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        EnterpriseIvr dbEnterpriseIvr = selectByPrimaryKey(enterpriseIvr.getId());
        if (dbEnterpriseIvr == null || !enterpriseIvr.getEnterpriseId().equals(dbEnterpriseIvr.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }
        return new CtiLinkApiResult<>(dbEnterpriseIvr);
    }


    private CtiLinkApiResult<EnterpriseIvr> validateEnterpriseIvr(EnterpriseIvr enterpriseIvr) {
        if (enterpriseIvr.getIvrId() == null || enterpriseIvr.getIvrId() < 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[ivrId]不正确");
        }
        if (StringUtils.isEmpty(enterpriseIvr.getPathName())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[pathName]不能为空");
        }
        enterpriseIvr.setPathName(SqlUtil.escapeSql(enterpriseIvr.getPathName()));

        if (StringUtils.isEmpty(enterpriseIvr.getProperty())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[property]不能为空");
        }

        //验证property格式?

        if (enterpriseIvr.getId() == null) {  //新增
            if (StringUtils.isEmpty(enterpriseIvr.getPath())) {
                return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[path]不能为空");
            }
            if (enterpriseIvr.getParentId() == null || enterpriseIvr.getParentId() < 0) {
                return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[parentId]不正确");
            }
            //根节点判断
            if (enterpriseIvr.getParentId() == 0 && !enterpriseIvr.getPath().equals("1")) {
                return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "根节点的path必须等于1");
            }
            if (enterpriseIvr.getAction() == null || enterpriseIvr.getAction() <= 0) {
                return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[action]不正确");
            }

            if (enterpriseIvr.getParentId() != 0) {
                EnterpriseIvr parentIvr  = selectByPrimaryKey(enterpriseIvr.getParentId());
                if (parentIvr == null) {
                    return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "父节点不存在");
                }
                if (!enterpriseIvr.getPath().startsWith(parentIvr.getPath() + ".")) {
                    return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[path]不正确");

                }
                if (!StringUtils.isNumeric(enterpriseIvr.getPath().substring(parentIvr.getPath().length() + 1
                        , enterpriseIvr.getPath().length()))) {
                    return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[path]不正确");
                }
            }
            //判断节点是否已经存在
            Condition condition = new Condition(EnterpriseIvr.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("enterpriseId", enterpriseIvr.getEnterpriseId());
            criteria.andEqualTo("parentId", enterpriseIvr.getParentId());
            criteria.andEqualTo("path", enterpriseIvr.getPath());
            int count = selectCountByCondition(condition);
            if (count > 0) {
                return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "节点已存在");
            }
        }
        //判断ivrId是否存在
        IvrProfile ivrProfile = ivrProfileDao.selectByPrimaryKey(enterpriseIvr.getIvrId());
        if (ivrProfile == null || !enterpriseIvr.getEnterpriseId().equals(ivrProfile.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[ivrId]不正确");
        }
        enterpriseIvr.setAnchor(SqlUtil.escapeSql(enterpriseIvr.getAnchor()));

        return null;
    }


    public void refreshCache(EnterpriseIvr enterpriseIvr) {
        Condition condition = new Condition(EnterpriseIvr.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseIvr.getEnterpriseId());
        criteria.andEqualTo("ivrId", enterpriseIvr.getIvrId());
        condition.setOrderByClause("path");
        List<EnterpriseIvr> list = selectByCondition(condition);
        //语音导航
        if (list == null || list.isEmpty()) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_IVR_ENTERPRISE_ID_IVR_ID
                    , enterpriseIvr.getEnterpriseId(), enterpriseIvr.getIvrId()));
        } else {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_IVR_ENTERPRISE_ID_IVR_ID
                    , enterpriseIvr.getEnterpriseId(), enterpriseIvr.getIvrId()), list);
        }
    }

    private void setRefreshCacheMethod(EnterpriseIvr enterpriseIvr) {
        try {
            Method method = this.getClass().getMethod("refreshCache", EnterpriseIvr.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, enterpriseIvr);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("CtiLinkEnterpriseIvrServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }
}
