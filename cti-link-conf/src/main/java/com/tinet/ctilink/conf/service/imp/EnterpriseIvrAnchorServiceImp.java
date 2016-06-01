package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EnterpriseIvrMapper;
import com.tinet.ctilink.conf.model.CtiLinkEnterpriseIvrAnchor;
import com.tinet.ctilink.conf.model.EnterpriseIvr;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseIvrAnchorService;
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
 * @date 16/5/17 09:19
 */
@Service
public class EnterpriseIvrAnchorServiceImp extends BaseService<CtiLinkEnterpriseIvrAnchor> implements CtiLinkEnterpriseIvrAnchorService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private EnterpriseIvrMapper enterpriseIvrMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<CtiLinkEnterpriseIvrAnchor> createEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseIvrAnchor.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        enterpriseIvrAnchor.setId(null);
        if (enterpriseIvrAnchor.getIvrId() == null || enterpriseIvrAnchor.getIvrId() < 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[ivrId]不正确");
        }
        if (StringUtils.isEmpty(enterpriseIvrAnchor.getPath())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[path]不能为空");
        }
        if (StringUtils.isEmpty(enterpriseIvrAnchor.getEvent())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[event]不正确");
        }
        if (StringUtils.isEmpty(enterpriseIvrAnchor.getData())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[data]不能为空");
        }

        //判断 ivrid和path是否存在
        Condition condition = new Condition(EnterpriseIvr.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseIvrAnchor.getEnterpriseId());
        criteria.andEqualTo("ivrId", enterpriseIvrAnchor.getIvrId());
        criteria.andEqualTo("path", enterpriseIvrAnchor.getPath());
        List<EnterpriseIvr> enterpriseIvrList = enterpriseIvrMapper.selectByCondition(condition);
        if (enterpriseIvrList == null || enterpriseIvrList.isEmpty()) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[ivrId]或[path]不正确");
        }

        //检查 event data
        enterpriseIvrAnchor.setData(SqlUtil.escapeSql(enterpriseIvrAnchor.getData()));
        enterpriseIvrAnchor.setEvent(SqlUtil.escapeSql(enterpriseIvrAnchor.getEvent()));
        enterpriseIvrAnchor.setCreateTime(new Date());
        int count = insertSelective(enterpriseIvrAnchor);
        if (count != 1) {
            logger.error("EnterpriseIvrAnchorServiceImp.createEnterpriseIvrAnchor error, " + enterpriseIvrAnchor + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
        } else {
            setRefreshCacheMethod(enterpriseIvrAnchor);
            return new ApiResult<>(enterpriseIvrAnchor);
        }
    }

    @Override
    public ApiResult deleteEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseIvrAnchor.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (enterpriseIvrAnchor.getId() == null
                || enterpriseIvrAnchor.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        CtiLinkEnterpriseIvrAnchor dbEnterpriseIvrAnchor = selectByPrimaryKey(enterpriseIvrAnchor.getId());

        if (dbEnterpriseIvrAnchor == null
                || !enterpriseIvrAnchor.getEnterpriseId().equals(dbEnterpriseIvrAnchor.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }
        //递归删除ivr节点
        int count = deleteByPrimaryKey(enterpriseIvrAnchor.getId());

        if (count <= 0) {
            logger.error("EnterpriseIvrServiceImp.deleteEnterpriseIvr error, " + enterpriseIvrAnchor + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "删除失败");
        }

        setRefreshCacheMethod(dbEnterpriseIvrAnchor);
        return new ApiResult<>(ApiResult.SUCCESS_RESULT);
    }

    @Override
    public ApiResult<CtiLinkEnterpriseIvrAnchor> updateEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseIvrAnchor.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        if (enterpriseIvrAnchor.getId() == null
                || enterpriseIvrAnchor.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        CtiLinkEnterpriseIvrAnchor dbEnterpriseIvrAnchor = selectByPrimaryKey(enterpriseIvrAnchor.getId());

        if (dbEnterpriseIvrAnchor == null
                || !enterpriseIvrAnchor.getEnterpriseId().equals(dbEnterpriseIvrAnchor.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        //更新
        dbEnterpriseIvrAnchor.setData(SqlUtil.escapeSql(enterpriseIvrAnchor.getData()));
        dbEnterpriseIvrAnchor.setEvent(SqlUtil.escapeSql(enterpriseIvrAnchor.getEvent()));
        int count = updateByPrimaryKeySelective(dbEnterpriseIvrAnchor);

        if (count != 1) {
            logger.error("EnterpriseIvrAnchorServiceImp.updateEnterpriseIvrAnchor error, " + dbEnterpriseIvrAnchor + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }
        setRefreshCacheMethod(dbEnterpriseIvrAnchor);
        return new ApiResult<>(dbEnterpriseIvrAnchor);
    }

    @Override
    public ApiResult<List<CtiLinkEnterpriseIvrAnchor>> listEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseIvrAnchor.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (enterpriseIvrAnchor.getIvrId() == null || enterpriseIvrAnchor.getIvrId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[ivrId]不正确");
        }
        Condition condition = new Condition(CtiLinkEnterpriseIvrAnchor.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseIvrAnchor.getEnterpriseId());
        criteria.andEqualTo("ivrId", enterpriseIvrAnchor.getIvrId());
        condition.setOrderByClause("path");
        List<CtiLinkEnterpriseIvrAnchor> list = selectByCondition(condition);
        return new ApiResult<>(list);
    }

    @Override
    public ApiResult<CtiLinkEnterpriseIvrAnchor> getEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseIvrAnchor.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        if (enterpriseIvrAnchor.getId() == null || enterpriseIvrAnchor.getId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        CtiLinkEnterpriseIvrAnchor dbEnterpriseIvrAnchor = selectByPrimaryKey(enterpriseIvrAnchor.getId());
        if (dbEnterpriseIvrAnchor == null || !enterpriseIvrAnchor.getEnterpriseId().equals(dbEnterpriseIvrAnchor.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }
        return new ApiResult<>(dbEnterpriseIvrAnchor);
    }

    public void refreshCache(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        Condition condition = new Condition(CtiLinkEnterpriseIvrAnchor.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseIvrAnchor.getEnterpriseId());
        criteria.andEqualTo("ivrId", enterpriseIvrAnchor.getIvrId());
        condition.setOrderByClause("path");
        List<CtiLinkEnterpriseIvrAnchor> enterpriseIvrAnchorList = selectByCondition(condition);
        if (enterpriseIvrAnchorList != null) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_IVR_ANCHOR_ENTERPRISE_ID_IVR_ID
                    , enterpriseIvrAnchor.getEnterpriseId(), enterpriseIvrAnchor.getIvrId()), enterpriseIvrAnchorList);
        } else {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_IVR_ANCHOR_ENTERPRISE_ID_IVR_ID
                    , enterpriseIvrAnchor.getEnterpriseId(), enterpriseIvrAnchor.getIvrId()));
        }

    }

    private void setRefreshCacheMethod(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        try {
            Method method = this.getClass().getMethod("refreshCache", CtiLinkEnterpriseIvrAnchor.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, enterpriseIvrAnchor);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("EnterpriseIvrServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }
}
