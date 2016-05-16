package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.TelSetTel;
import com.tinet.ctilink.conf.service.v1.CtiLinkTelSetTelService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.List;
/**
 * @author huangbin
 * @date 16/4/14.
 */

@Service
public class CtiLinkTelSetTelServiceImp extends BaseService<TelSetTel> implements CtiLinkTelSetTelService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisService redisService;

    @Override
    public CtiLinkApiResult createTelSetTel(TelSetTel telSetTel) {
        if(telSetTel.getEnterpriseId()==null || telSetTel.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSetTel.getSetId()==null || telSetTel.getSetId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"电话组id不能为空");
        if(telSetTel.getTel()==null || "".equals(telSetTel.getTel().trim()))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"电话不能为控");
        if(telSetTel.getTimeout()==null || telSetTel.getTimeout()>60 || telSetTel.getTimeout()<5)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"超时时间为5-60秒");
        if(telSetTel.getPriority()==null || telSetTel.getPriority()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"优先级不能为空");
        int success = insertSelective(telSetTel);

        if(success==1) {
            setRefreshCacheMethod("setCache",telSetTel);
            return new CtiLinkApiResult<>(CtiLinkApiResult.SUCCESS_RESULT, "成功");
        }
        logger.error("CtiLinkTelSetTelServiceImp.createTelSetTel error " + telSetTel + "success=" + success );
        return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"失败");
    }

    @Override
    public CtiLinkApiResult deleteTelSetTel(TelSetTel telSetTel) {
        if(telSetTel.getEnterpriseId()==null || telSetTel.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSetTel.getId()==null || telSetTel.getId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"电话组电话id不能为空");

        Condition condition = new Condition(TelSetTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("id",telSetTel.getId());
        criteria.andEqualTo("enterpriseId",telSetTel.getEnterpriseId());
        int success = deleteByCondition(condition);

        if(success==1) {
            setRefreshCacheMethod("deleteCache",telSetTel);
            return new CtiLinkApiResult<>(CtiLinkApiResult.SUCCESS_RESULT, "删除成功");
        }
        logger.error("CtiLinkTelSetTelServiceImp.deleteTelSetTel error " + telSetTel + "success=" + success );
        return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public CtiLinkApiResult updateTelSetTel(TelSetTel telSetTel) {
        if(telSetTel.getEnterpriseId()==null || telSetTel.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSetTel.getId()==null || telSetTel.getId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"电话组电话id不能为空");
        if(telSetTel.getTel()==null || "".equals(telSetTel.getTel().trim()))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"电话不能为空");
        if(telSetTel.getTimeout()==null || telSetTel.getTimeout()>60 || telSetTel.getTimeout()<5)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"超时时间为5-60秒");
        if(telSetTel.getPriority()==null || telSetTel.getPriority()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"优先级不能为空");

        int success = updateByPrimaryKeySelective(telSetTel);
        if(success==1) {
            setRefreshCacheMethod("setCache",telSetTel);
            return new CtiLinkApiResult<>(CtiLinkApiResult.SUCCESS_RESULT, "更新成功");
        }
        logger.error("CtiLinkTelSetTelServiceImp.updateTelSetTel error " + telSetTel + "success=" + success);
        return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public CtiLinkApiResult<List<TelSetTel>> listTelSetTel(TelSetTel telSetTel) {
        if(telSetTel.getEnterpriseId()==null || telSetTel.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSetTel.getSetId()==null || telSetTel.getSetId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"电话组id不能为空");

        Condition condition = new Condition(TelSetTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", telSetTel.getEnterpriseId());
        criteria.andEqualTo("setId", telSetTel.getSetId());
        List<TelSetTel> telSetTelsList = selectByCondition(condition);

        if(telSetTelsList!=null && telSetTelsList.size()>0)
            return new CtiLinkApiResult<>(telSetTelsList);
        return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"获取电话列表失败");
    }

    protected String getKey(TelSetTel telSetTel) {
        return String.format(CacheKey.TEL_SET_TEL_ENTERPRISE_TSNO,
                telSetTel.getEnterpriseId(),  telSetTel.getTelName());
    }

    public void deleteCache(TelSetTel telSetTel){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(telSetTel));
    }

    public void setCache(TelSetTel telSetTel){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(telSetTel), telSetTel);
    }

    private void setRefreshCacheMethod(String methodName, TelSetTel telSetTel){
        try {
            Method method = this.getClass().getMethod(methodName, TelSetTel.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,telSetTel);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch (Exception e) {
            logger.error("CtiLinkTelSetTelServiceImp.setRefreshCacheMethod error,cache refresh fail," + "class=" +
                    this.getClass().getName(), e);
        }
    }
}