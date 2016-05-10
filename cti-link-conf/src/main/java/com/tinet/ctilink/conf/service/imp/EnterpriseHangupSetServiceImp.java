package com.tinet.ctilink.conf.service.imp;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseHangupSet;
import com.tinet.ctilink.conf.service.AbstractService;
import com.tinet.ctilink.conf.service.v1.EnterpriseHangupSetService;
import com.tinet.ctilink.inc.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * Created by nope-J on 2016/5/4.
 */
public class EnterpriseHangupSetServiceImp extends AbstractService<EnterpriseHangupSet> implements EnterpriseHangupSetService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<EnterpriseHangupSet> createEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet) {
        if(enterpriseHangupSet.getEnterpriseId() == null || enterpriseHangupSet.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseHangupSet.getType() == null || !(enterpriseHangupSet.getType()==0 || enterpriseHangupSet.getType()==1))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"呼叫类型：1.呼入 2。外呼");
        if(enterpriseHangupSet.getVariableName().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"变量名称不能为空");
        if(enterpriseHangupSet.getVariableValue().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"变量值不能为空");
        if(enterpriseHangupSet.getVariableValueType() == null || !(enterpriseHangupSet.getVariableValueType()==0 || enterpriseHangupSet.getVariableValueType()==1))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"变量类型为：0.表达式 1.字符串");
        if(enterpriseHangupSet.getSort() == null || enterpriseHangupSet.getSort() < 1)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"排序，从 1 开始");
        enterpriseHangupSet.setCreateTime(new Date());
        int success = insertSelective(enterpriseHangupSet);

        if(success == 1){
            setRefreshCacheMethod("setCache",enterpriseHangupSet);
            return new ApiResult<>(enterpriseHangupSet);
        }
        logger.error("EnterpriseHangupSetServiceImp.createEnterpriseHangupSet error " + enterpriseHangupSet + "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"新增失败");
    }

    @Override
    public ApiResult deleteEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet) {
        if(enterpriseHangupSet.getEnterpriseId() == null || enterpriseHangupSet.getEnterpriseId() <= 0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseHangupSet.getId() == null || enterpriseHangupSet.getId() <= 0)
            return new ApiResult(ApiResult.FAIL_RESULT,"推送id不正确");

        Condition condition = new Condition(EnterpriseHangupSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseHangupSet.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseHangupSet.getId());

        int success = deleteByCondition(condition);

        if(success == 1){
            setRefreshCacheMethod("deleteCache",enterpriseHangupSet);
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("EnterpriseHangupSetServiceImp.deleteEnterpriseHangupSet error "+ enterpriseHangupSet +"success=" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult<List<EnterpriseHangupSet>> listEnterpriseHangupSet(EnterpriseHangupSet enterpriseHangupSet) {
        if(enterpriseHangupSet.getEnterpriseId() == null || enterpriseHangupSet.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");

        Condition condition = new Condition(EnterpriseHangupSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseHangupSet.getEnterpriseId());

        List<EnterpriseHangupSet> enterpriseHangupSetList = selectByCondition(condition);

        if(enterpriseHangupSetList != null && enterpriseHangupSetList.size() > 0)
            return new ApiResult<>(enterpriseHangupSetList);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"获取推送变量列表失败");
    }

    @Override
    protected List<EnterpriseHangupSet> select(Integer enterpriseId) {
        return null;
    }

    @Override
    protected String getKey(EnterpriseHangupSet enterpriseHangupSet) {
        return String.format(CacheKey.ENTERPRISE_HANGUP_SET_ENTERPRISE_ID_TYPE,enterpriseHangupSet.getEnterpriseId(),enterpriseHangupSet.getType());
    }

    @Override
    protected String getCleanKeyPrefix() {
        return null;
    }

    @Override
    protected String getRefreshKeyPrefix(Integer enterpriseId) {
        return null;
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
            ProviderFilter.methodThreadLocal.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("EnterpriseHangupSetServiceImp.setRefreshCacheMethod error refresh cache fail class=" + this.getClass().getName(),e);
        }
    }
}
