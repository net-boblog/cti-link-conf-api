package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.sun.tools.javac.comp.Enter;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseArea;
import com.tinet.ctilink.conf.service.AbstractService;
import com.tinet.ctilink.conf.service.v1.EnterpriseAreaService;
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
 * @author huangbin //
 * @date 2016/4/18. //
 */

@Service
public class EnterpriseAreaServiceImp extends AbstractService<EnterpriseArea> implements EnterpriseAreaService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult createEnterpriseArea(EnterpriseArea enterpriseArea) {
        if(enterpriseArea.getEnterpriseId()==null || enterpriseArea.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseArea.getGroupId()==null || enterpriseArea.getGroupId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组号不正确");
        if(enterpriseArea.getAreaCode().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"地区区号不能为空");
        if(enterpriseArea.getProvince().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"省份不能为空");
        if(enterpriseArea.getCity().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"城市不能为空");
        enterpriseArea.setCreateTime(new Date());

        int success = insertSelective(enterpriseArea);
        if(success==1) {
            setRefreshCacheMethod("setCache",enterpriseArea);
            return new ApiResult(enterpriseArea);
        }

        logger.error("EnterpriseAreaServiceImp.createEnterpriseArea error " + enterpriseArea + "sueccess" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"增加失败");
    }

    @Override
    public ApiResult deleteEnterpriseArea(EnterpriseArea enterpriseArea) {
        if(enterpriseArea.getEnterpriseId()==null || enterpriseArea.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseArea.getId()==null || enterpriseArea.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组地区id不能为空");

        Condition condition = new Condition(EnterpriseArea.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseArea.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseArea.getId());
        int success = deleteByCondition(enterpriseArea);

        if(success==1) {
            setRefreshCacheMethod("deleteCache",enterpriseArea);
            return new ApiResult(ApiResult.SUCCESS_RESULT, ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("EnterpriseAreaServiceImp.deleteEnterpriseArea.deleteEnterpriseArea error " + enterpriseArea + "success" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");

    }

    @Override
    public ApiResult<List<EnterpriseArea>> listEnterpriseArea(EnterpriseArea enterpriseArea) {
        if(enterpriseArea.getEnterpriseId()==null || enterpriseArea.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业id不能为空");
        if(enterpriseArea.getGroupId()==null || enterpriseArea.getGroupId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组id不能为空");

        Condition condition = new Condition(EnterpriseArea.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseArea.getEnterpriseId());
        criteria.andEqualTo("groupId",enterpriseArea.getGroupId());
        List<EnterpriseArea> enterpriseAreaList = selectByCondition(condition);

        if(enterpriseAreaList != null && enterpriseAreaList.size() > 0)
            return new ApiResult<>(enterpriseAreaList);
        return new ApiResult(ApiResult.FAIL_RESULT,"获取地区列表失败");
    }

    @Override
    protected List<EnterpriseArea> select(Integer enterpriseId) {
        return null;
    }

    @Override
    protected String getKey(EnterpriseArea enterpriseArea) {
        return String.format(CacheKey.ENTERPRISE_AREA_ENTERPRISE_ID_GROUP_ID_AREA_CODE,enterpriseArea.getEnterpriseId(),
                enterpriseArea.getGroupId(),enterpriseArea.getAreaCode());
    }

    @Override
    protected String getCleanKeyPrefix() {
        return null;
    }

    @Override
    protected String getRefreshKeyPrefix(Integer enterpriseId) {
        return null;
    }

    public void setCache(EnterpriseArea enterpriseArea){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseArea),enterpriseArea);
    }

    public void deleteCache(EnterpriseArea enterpriseArea){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseArea));
    }

    private void setRefreshCacheMethod(String methodName, EnterpriseArea enterpriseArea){
        try {
            Method method = this.getClass().getMethod(methodName, EnterpriseArea.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,enterpriseArea);
            ProviderFilter.methodThreadLocal.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("EnterpriseAreaServiceImp setRefreshMethod error refresh cache fail class=" +
                    this.getClass().getName());
        }
    }
}