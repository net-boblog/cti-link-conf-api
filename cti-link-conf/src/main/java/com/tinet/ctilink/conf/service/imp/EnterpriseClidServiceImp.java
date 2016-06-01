package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseClid;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseClidService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * @author fengwei //
 * @date 16/5/31 17:10
 */
@Service
public class EnterpriseClidServiceImp extends BaseService<EnterpriseClid> implements CtiLinkEnterpriseClidService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<EnterpriseClid> updateEnterpriseClid(EnterpriseClid enterpriseClid) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseClid.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        ApiResult<EnterpriseClid> result = validateEnterpriseClid(enterpriseClid);
        if (result != null) {
            return result;
        }
        EnterpriseClid dbEnterpriseClid = selectByPrimaryKey(enterpriseClid.getId());
        if (dbEnterpriseClid == null || !enterpriseClid.getEnterpriseId().equals(dbEnterpriseClid.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        enterpriseClid.setCreateTime(dbEnterpriseClid.getCreateTime());
        int count = updateByPrimaryKeySelective(enterpriseClid);

        if (count != 1) {
            logger.error("EnterpriseClidServiceImp.updateEnterpriseClid error, " + enterpriseClid + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }

        setRefreshCacheMethod("setCache", enterpriseClid);
        return new ApiResult<>(enterpriseClid);
    }

    private <T> ApiResult<T> validateEnterpriseClid(EnterpriseClid enterpriseClid) {
        if (enterpriseClid.getId() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }
        //判断类型, 1中继  2客户  3固定  4热线
        if (enterpriseClid.getIbClidRightType() <=0 || enterpriseClid.getIbClidRightType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[ibClidRightType]不正确");
        }
        if (enterpriseClid.getObPredictiveClidLeftType() <= 0 || enterpriseClid.getObPredictiveClidLeftType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPredictiveClidLeftType]不正确");
        }
        if (enterpriseClid.getObPredictiveClidRightType() <= 0 || enterpriseClid.getObPredictiveClidRightType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPredictiveClidRightType]不正确");
        }
        if (enterpriseClid.getObPreviewClidLeftType() <= 0 || enterpriseClid.getObPreviewClidLeftType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPreviewClidLeftType]不正确");
        }
        if (enterpriseClid.getObPreviewClidRightType() <= 0 || enterpriseClid.getObPreviewClidRightType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPreviewClidRightType]不正确");
        }

        //判断号码
        if (enterpriseClid.getIbClidRightType() == 1) {

        }
        return null;
    }

    public void setCache(EnterpriseClid enterpriseClid) {
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_CLID_ENTERPRISE_ID
                , enterpriseClid.getEnterpriseId()), enterpriseClid);
    }

    private void setRefreshCacheMethod(String methodName, EnterpriseClid enterpriseClid) {
        try {
            Method method = this.getClass().getMethod(methodName, EnterpriseClid.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, enterpriseClid);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("EnterpriseClidServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }

}
