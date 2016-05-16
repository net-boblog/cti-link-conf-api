package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.dao.EnterpriseIvrDao;
import com.tinet.ctilink.conf.dao.EnterpriseIvrRouterDao;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseIvr;
import com.tinet.ctilink.conf.model.EnterpriseIvrRouter;
import com.tinet.ctilink.conf.model.IvrProfile;
import com.tinet.ctilink.conf.service.v1.CtiLinkIvrProfileService;
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
 * @date 16/4/22 13:42
 */
@Service
public class CtiLinkIvrProfileServiceImp extends BaseService<IvrProfile> implements CtiLinkIvrProfileService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private EnterpriseIvrDao enterpriseIvrDao;

    @Autowired
    private EnterpriseIvrRouterDao enterpriseIvrRouterDao;

    @Autowired
    private RedisService redisService;

    @Override
    public CtiLinkApiResult<IvrProfile> createIvrProfile(IvrProfile ivrProfile) {
        //验证enterpriseId
        if (!entityDao.validateEntity(ivrProfile.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (StringUtils.isEmpty(ivrProfile.getIvrName())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[ivrName]不能为空");
        }
        ivrProfile.setIvrName(SqlUtil.escapeSql(ivrProfile.getIvrName()));
        if (StringUtils.isEmpty(ivrProfile.getIvrType())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[ivrType]不能为空");
        }
        if (!ivrProfile.getIvrType().equals("1")
                && !ivrProfile.getIvrType().equals("2")) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[ivrType]格式不正确");
        }
        ivrProfile.setIvrDescription(SqlUtil.escapeSql(ivrProfile.getIvrDescription()));

        ivrProfile.setCreateTime(new Date());
        int count = insertSelective(ivrProfile);
        if (count != 1) {
            logger.error("CtiLinkIvrProfileServiceImp.createIvrProfile error, " + ivrProfile + ", count=" + count);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "新增失败");
        } else {
            setRefreshCacheMethod("setCache", ivrProfile);
            return new CtiLinkApiResult<>(ivrProfile);
        }
    }

    @Override
    public CtiLinkApiResult deleteIvrProfile(IvrProfile ivrProfile) {
        //验证enterpriseId
        if (!entityDao.validateEntity(ivrProfile.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (ivrProfile.getId() == null || ivrProfile.getId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        IvrProfile dbIvrProfile = selectByPrimaryKey(ivrProfile.getId());
        if (dbIvrProfile == null || !ivrProfile.getEnterpriseId().equals(dbIvrProfile.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }
        Condition condition = new Condition(EnterpriseIvrRouter.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", ivrProfile.getEnterpriseId());
        criteria.andEqualTo("routerType", Const.ENTERPRISE_IVR_ROUTER_TYPE_IVR);
        criteria.andEqualTo("routerProperty", String.valueOf(ivrProfile.getId()));
        int count = enterpriseIvrRouterDao.selectCountByCondition(condition);
        if (count > 0 ) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "语音导航正在使用中");
        }
        //删除enterprise_ivr
        Condition eiCondition = new Condition(EnterpriseIvr.class);
        Condition.Criteria eiCriteria = condition.createCriteria();
        eiCriteria.andEqualTo("enterpriseId", ivrProfile.getEnterpriseId());
        eiCriteria.andEqualTo("ivrId", ivrProfile.getId());
        enterpriseIvrDao.deleteByCondition(eiCondition);

        count = deleteByPrimaryKey(ivrProfile.getId());
        if (count != 1) {
            logger.error("CtiLinkIvrProfileServiceImp.deleteIvrProfile error, " + ivrProfile + ", count=" + count);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "删除失败");
        }
        setRefreshCacheMethod("deleteCache", dbIvrProfile);
        return new CtiLinkApiResult<>(CtiLinkApiResult.SUCCESS_RESULT);
    }

    @Override
    public CtiLinkApiResult<IvrProfile> updateIvrProfile(IvrProfile ivrProfile) {
        //验证enterpriseId
        if (!entityDao.validateEntity(ivrProfile.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (ivrProfile.getId() == null || ivrProfile.getId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        if (StringUtils.isEmpty(ivrProfile.getIvrName())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[ivrName]不能为空");
        }
        ivrProfile.setIvrName(SqlUtil.escapeSql(ivrProfile.getIvrName()));
        ivrProfile.setIvrDescription(SqlUtil.escapeSql(ivrProfile.getIvrDescription()));

        IvrProfile dbIvrProfile = selectByPrimaryKey(ivrProfile.getId());
        if (dbIvrProfile == null || !ivrProfile.getEnterpriseId().equals(dbIvrProfile.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        dbIvrProfile.setIvrName(ivrProfile.getIvrName());
        dbIvrProfile.setIvrDescription(ivrProfile.getIvrDescription());
        int count = updateByPrimaryKeySelective(dbIvrProfile);
        if (count != 1) {
            logger.error("CtiLinkIvrProfileServiceImp.updateIvrProfile error, " + dbIvrProfile + ", count=" + count);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "更新失败");
        }
        setRefreshCacheMethod("setCache", dbIvrProfile);
        return new CtiLinkApiResult<>(dbIvrProfile);
    }

    @Override
    public CtiLinkApiResult<List<IvrProfile>> listIvrProfile(IvrProfile ivrProfile) {
        //验证enterpriseId
        if (!entityDao.validateEntity(ivrProfile.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        List<IvrProfile> list = select(ivrProfile.getEnterpriseId());

        return new CtiLinkApiResult<>(list);
    }

    @Override
    public CtiLinkApiResult<IvrProfile> getIvrProfile(IvrProfile ivrProfile) {
        //验证enterpriseId
        if (!entityDao.validateEntity(ivrProfile.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (ivrProfile.getId() == null || ivrProfile.getId() <= 0) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        IvrProfile dbIvrProfile = selectByPrimaryKey(ivrProfile.getId());
        if (dbIvrProfile == null || !ivrProfile.getEnterpriseId().equals(dbIvrProfile.getEnterpriseId())) {
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        return new CtiLinkApiResult<>(dbIvrProfile);
    }

    private List<IvrProfile> select(Integer enterpriseId) {
        Condition condition = new Condition(IvrProfile.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("id");
        return selectByCondition(condition);
    }

    private String getKey(IvrProfile ivrProfile) {
        return String.format(CacheKey.IVR_PROFILE_ENTERPRISE_ID_ID, ivrProfile.getEnterpriseId(), ivrProfile.getId());
    }

    public void deleteCache(IvrProfile ivrProfile) {
        //删除enterprise_ivr
        redisService.delete(Const.REDIS_DB_CONF_INDEX,
                String.format(CacheKey.ENTERPRISE_IVR_ENTERPRISE_ID_IVR_ID, ivrProfile.getEnterpriseId(), ivrProfile.getId()));
        //删除ivr_profile
        redisService.delete(Const.REDIS_DB_CONF_INDEX, getKey(ivrProfile));
    }

    public void setCache(IvrProfile ivrProfile) {
        redisService.set(Const.REDIS_DB_CONF_INDEX, getKey(ivrProfile), ivrProfile);
    }

    private void setRefreshCacheMethod(String methodName, IvrProfile ivrProfile) {
        try {
            Method method = this.getClass().getMethod(methodName, IvrProfile.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, ivrProfile);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("CtiLinkIvrProfileServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }
}
