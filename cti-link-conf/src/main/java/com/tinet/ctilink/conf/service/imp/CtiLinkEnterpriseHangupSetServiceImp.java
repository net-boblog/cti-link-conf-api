package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseHangupSet;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseHangupSetService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;


/**
 * @author huangbin
 * @date 2016/5/4.
 */

@Service
public class CtiLinkEnterpriseHangupSetServiceImp extends BaseService<EnterpriseHangupSet> implements CtiLinkEnterpriseHangupSetService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<EnterpriseHangupSet> createEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet) {
        if( ! entityDao.validateEntity(enterpriseHangupSet.getEnterpriseId()))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");

        ApiResult<EnterpriseHangupSet> result = validateEnterpriseHangupSet(enterpriseHangupSet);
        if(result != null)
            return result;

        enterpriseHangupSet.setCreateTime(new Date());

        int success = insertSelective(enterpriseHangupSet);
        if(success == 1){
            setRefreshCacheMethod("setCache",enterpriseHangupSet);
            return new ApiResult<>(enterpriseHangupSet);
        }
        logger.error("CtiLinkEnterpriseHangupSetServiceImp.createEnterpriseHangupSet error " + enterpriseHangupSet + "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"新增失败");
    }

    @Override
    public ApiResult deleteEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet) {
        if( ! entityDao.validateEntity(enterpriseHangupSet.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");

        if(enterpriseHangupSet.getId() == null || enterpriseHangupSet.getId() <= 0)
            return new ApiResult(ApiResult.FAIL_RESULT,"推送id不正确");

        Condition condition = new Condition(EnterpriseHangupSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseHangupSet.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseHangupSet.getId());

        EnterpriseHangupSet enterpriseHangupSet1 = null;
        List<EnterpriseHangupSet> enterpriseHangupSetList = selectByCondition(condition);
        if(enterpriseHangupSetList != null && enterpriseHangupSetList.size() > 0)
            enterpriseHangupSet1 = enterpriseHangupSetList.get(0);

        int success = deleteByCondition(condition);
        if(success == 1){
            setRefreshCacheMethod("deleteCache",enterpriseHangupSet1);
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("CtiLinkEnterpriseHangupSetServiceImp.deleteEnterpriseHangupSet error "+ enterpriseHangupSet +"success=" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult<List<EnterpriseHangupSet>> listEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet) {
        if( ! entityDao.validateEntity(enterpriseHangupSet.getEnterpriseId()))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");

        Condition condition = new Condition(EnterpriseHangupSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseHangupSet.getEnterpriseId());

        List<EnterpriseHangupSet> enterpriseHangupSetList = selectByCondition(condition);

        if(enterpriseHangupSetList != null && enterpriseHangupSetList.size() > 0)
            return new ApiResult<>(enterpriseHangupSetList);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"获取推送变量列表失败");
    }

    protected String getKey(EnterpriseHangupSet enterpriseHangupSet) {
        return String.format(CacheKey.ENTERPRISE_HANGUP_SET_ENTERPRISE_ID_TYPE,enterpriseHangupSet.getEnterpriseId(),enterpriseHangupSet.getType());
    }

    public void setCache(EnterpriseHangupSet enterpriseHangupSet){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseHangupSet),enterpriseHangupSet);
    }

    public void deleteCache(EnterpriseHangupSet enterpriseHangupSet){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseHangupSet));
    }

    private void setRefreshCacheMethod(String methodName ,EnterpriseHangupSet enterpriseHangupSet){
        try{
            Method method = this.getClass().getMethod(methodName,EnterpriseHangupSet.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,enterpriseHangupSet);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("CtiLinkEnterpriseHangupSetServiceImp.setRefreshCacheMethod error refresh cache fail class=" + this.getClass().getName(),e);
        }
    }

    private <T> ApiResult<T> validateEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet){
        if(enterpriseHangupSet.getType() == null || !(enterpriseHangupSet.getType()==0 || enterpriseHangupSet.getType()==1))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"呼叫类型：1.呼入 2。外呼");
        if(StringUtils.isEmpty(enterpriseHangupSet.getVariableName()))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"变量名称不能为空");
        if(StringUtils.isEmpty(enterpriseHangupSet.getVariableValue()))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"变量值不能为空");
        if(enterpriseHangupSet.getVariableValueType() == null || !(enterpriseHangupSet.getVariableValueType()==0 || enterpriseHangupSet.getVariableValueType()==1))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"变量类型为：0.表达式 1.字符串");
        if(enterpriseHangupSet.getSort() == null || enterpriseHangupSet.getSort() < 1)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"排序，从 1 开始");

        return null;
    }
}
