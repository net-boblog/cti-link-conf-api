package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseArea;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseAreaService;
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
public class CtiLinkEnterpriseAreaServiceImp extends BaseService<EnterpriseArea> implements CtiLinkEnterpriseAreaService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Override
    public CtiLinkApiResult createEnterpriseArea(EnterpriseArea enterpriseArea) {
        if(enterpriseArea.getEnterpriseId()==null || enterpriseArea.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseArea.getGroupId()==null || enterpriseArea.getGroupId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区组号不正确");
        if(enterpriseArea.getAreaCode().isEmpty())
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区区号不能为空");
        if(enterpriseArea.getProvince().isEmpty())
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"省份不能为空");
        if(enterpriseArea.getCity().isEmpty())
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"城市不能为空");
        enterpriseArea.setCreateTime(new Date());

        int success = insertSelective(enterpriseArea);
        if(success==1) {
            setRefreshCacheMethod("setCache",enterpriseArea);
            return new CtiLinkApiResult(enterpriseArea);
        }

        logger.error("CtiLinkEnterpriseAreaServiceImp.createEnterpriseArea error " + enterpriseArea + "sueccess" + success);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"增加失败");
    }

    @Override
    public CtiLinkApiResult deleteEnterpriseArea(EnterpriseArea enterpriseArea) {
        if(enterpriseArea.getEnterpriseId()==null || enterpriseArea.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseArea.getId()==null || enterpriseArea.getId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区组地区id不能为空");

        Condition condition = new Condition(EnterpriseArea.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseArea.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseArea.getId());
        int success = deleteByCondition(enterpriseArea);

        if(success==1) {
            setRefreshCacheMethod("deleteCache",enterpriseArea);
            return new CtiLinkApiResult(CtiLinkApiResult.SUCCESS_RESULT, CtiLinkApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("CtiLinkEnterpriseAreaServiceImp.deleteEnterpriseArea.deleteEnterpriseArea error " + enterpriseArea + "success" + success);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"删除失败");

    }

    @Override
    public CtiLinkApiResult<List<EnterpriseArea>> listEnterpriseArea(EnterpriseArea enterpriseArea) {
        if(enterpriseArea.getEnterpriseId()==null || enterpriseArea.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业id不能为空");
        if(enterpriseArea.getGroupId()==null || enterpriseArea.getGroupId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区组id不能为空");

        Condition condition = new Condition(EnterpriseArea.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseArea.getEnterpriseId());
        criteria.andEqualTo("groupId",enterpriseArea.getGroupId());
        List<EnterpriseArea> enterpriseAreaList = selectByCondition(condition);

        if(enterpriseAreaList != null && enterpriseAreaList.size() > 0)
            return new CtiLinkApiResult<>(enterpriseAreaList);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"获取地区列表失败");
    }

    protected String getKey(EnterpriseArea enterpriseArea) {
        return String.format(CacheKey.ENTERPRISE_AREA_ENTERPRISE_ID_GROUP_ID_AREA_CODE,enterpriseArea.getEnterpriseId(),
                enterpriseArea.getGroupId(),enterpriseArea.getAreaCode());
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
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("CtiLinkEnterpriseAreaServiceImp setRefreshMethod error refresh cache fail class=" +
                    this.getClass().getName());
        }
    }
}