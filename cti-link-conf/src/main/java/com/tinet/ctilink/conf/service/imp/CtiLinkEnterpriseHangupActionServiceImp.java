package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseHangupAction;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseHangupActionService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;
import sun.misc.*;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * Created by nope-J on 2016/4/29.
 */
@Service
public class CtiLinkEnterpriseHangupActionServiceImp extends BaseService<EnterpriseHangupAction> implements CtiLinkEnterpriseHangupActionService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<EnterpriseHangupAction> createEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction) {
        if(enterpriseHangupAction.getEnterpriseId()==null || enterpriseHangupAction.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编码不正确");
        if(enterpriseHangupAction.getUrl().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"推送地址不能为空");

        if( ! enterpriseHangupAction.getParamName().isEmpty()){
            byte[] nameByte;
            String[] name = enterpriseHangupAction.getParamName().split(",");
            String[] base64Name = new String[name.length];
            String paramName;
            BASE64Encoder base64Encoder = new BASE64Encoder();

            //加密
            for (int i = 0; i < name.length; i++) {
                nameByte = name[i].getBytes();
                if (nameByte != null)
                    base64Name[i] = base64Encoder.encode(nameByte);
            }
            paramName = base64Name[0];
            for(int i=1; i<base64Name.length;i++)
                if( ! base64Name[i].isEmpty())
                    paramName +=","+base64Name[i];
            enterpriseHangupAction.setParamName(paramName);
        }

        if( ! enterpriseHangupAction.getParamVariable().isEmpty()){
            byte[] variableByte;
            String[] variable = enterpriseHangupAction.getParamVariable().split(",");
            String[] base64Variable = new String[variable.length];
            String paramVariable;
            BASE64Encoder base64Encoder = new BASE64Encoder();

            //加密
            for (int i = 0; i < variable.length; i++) {
                variableByte = variable[i].getBytes();
                base64Variable[i] = base64Encoder.encode(variableByte);
            }

            paramVariable = base64Variable[0];
            for(int i=1; i<base64Variable.length;i++)
                paramVariable +=","+base64Variable[i];

            String[] paramName = enterpriseHangupAction.getParamName().split(",");
            if(paramName.length != base64Variable.length)
                return new ApiResult<>(ApiResult.FAIL_RESULT,"推送参数和值不匹配");
            enterpriseHangupAction.setParamVariable(paramVariable);
        }

        if(enterpriseHangupAction.getTimeout()>30 || enterpriseHangupAction.getTimeout()<0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"超时时间不能大于30");
        if(enterpriseHangupAction.getRetry() == null || enterpriseHangupAction.getRetry() < 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"请选择重试次数");
        if(enterpriseHangupAction.getType() == null || !(enterpriseHangupAction.getType()==1
        || enterpriseHangupAction.getType()==2 || enterpriseHangupAction.getType()==3
                || enterpriseHangupAction.getType()==4 || enterpriseHangupAction.getType()==5
                || enterpriseHangupAction.getType()==6 || enterpriseHangupAction.getType()==7
                || enterpriseHangupAction.getType()==8 || enterpriseHangupAction.getType()==9))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"推送类型：1.呼入来电推送2.呼入呼转响铃推送3.呼入呼转接通推送4.呼入挂机推送" +
                    "5.外呼响铃推送6.外呼呼转响铃推送7.外呼接通推送8.外呼挂机推送9.按键推送");
        if(enterpriseHangupAction.getMethod() == null || !(enterpriseHangupAction.getMethod()==1 || enterpriseHangupAction.getMethod()==2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"推送方法：1.POST 2.GET");
        enterpriseHangupAction.setCreateTime(new Date());
        int success = insertSelective(enterpriseHangupAction);

        if(success == 1){
            setRefreshCacheMethod("setCache",enterpriseHangupAction);
            return new ApiResult<>(enterpriseHangupAction);
        }
        logger.error("CtiLinkEnterpriseHangupActionServiceImp.createEnterpriseHangupAction error refresh cache fail "+ enterpriseHangupAction +
        "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public ApiResult deleteEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction) {
        EnterpriseHangupAction eha = selectByPrimaryKey(enterpriseHangupAction.getId());
        if(enterpriseHangupAction.getEnterpriseId()==null || enterpriseHangupAction.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseHangupAction.getId()==null || enterpriseHangupAction.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"id不正确");
        if( ! eha.getEnterpriseId().equals(enterpriseHangupAction.getEnterpriseId()))
            return new ApiResult("id和企业编号不对应");

        Condition condition = new Condition(EnterpriseHangupAction.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseHangupAction.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseHangupAction.getId());

        int success = deleteByCondition(condition);

        if(success == 1){
            setRefreshCacheMethod("deleteCache",enterpriseHangupAction);
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("CtiLinkEnterpriseHangupActionServiceImp.deleteEnterpriseHangupAction error "+ enterpriseHangupAction +
        "success=" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult<EnterpriseHangupAction> updateEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction) {
        if (enterpriseHangupAction.getId()==null || enterpriseHangupAction.getId()<=0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"id不正确");

        EnterpriseHangupAction eha = selectByPrimaryKey(enterpriseHangupAction.getId());

        if(enterpriseHangupAction.getEnterpriseId()==null || enterpriseHangupAction.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编码不正确");
        if(enterpriseHangupAction.getUrl().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"推送地址不能为空");
        if( ! enterpriseHangupAction.getParamName().isEmpty()){
            byte[] nameByte;
            String[] name = enterpriseHangupAction.getParamName().split(",");
            String[] base64Name = new String[name.length];
            String paramName = "";
            BASE64Encoder base64Encoder = new BASE64Encoder();

            //加密
            for (int i = 0; i < name.length; i++) {
                nameByte = name[i].getBytes();
                if (nameByte != null)
                    base64Name[i] = base64Encoder.encode(nameByte);
            }
            paramName = base64Name[0];
            for(int i=1; i<base64Name.length;i++)
                paramName +=","+base64Name[i];
            enterpriseHangupAction.setParamName(paramName);
        }

        if( ! enterpriseHangupAction.getParamVariable().isEmpty()){
            byte[] variableByte;
            String[] variable = enterpriseHangupAction.getParamVariable().split(",");
            String[] base64Variable = new String[variable.length];
            String paramVariable = "";
            BASE64Encoder base64Encoder = new BASE64Encoder();

            //加密
            for (int i = 0; i < variable.length; i++) {
                variableByte = variable[i].getBytes();
                if (variableByte != null)
                    base64Variable[i] = base64Encoder.encode(variableByte);
            }

            paramVariable = base64Variable[0];
            for(int i=1; i<base64Variable.length;i++)
                paramVariable +=","+base64Variable[i];

            String[] paramName = enterpriseHangupAction.getParamName().split(",");
            if(paramName.length != base64Variable.length)
                return new ApiResult<>(ApiResult.FAIL_RESULT,"推送参数和值不匹配");
            enterpriseHangupAction.setParamVariable(paramVariable);
        }

        if(enterpriseHangupAction.getTimeout()>30 || enterpriseHangupAction.getTimeout()<0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"超时时间不能大于30");
        if(enterpriseHangupAction.getRetry() == null || enterpriseHangupAction.getRetry() < 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"请选择重试次数");
        if(enterpriseHangupAction.getRetry() == null || !(enterpriseHangupAction.getType()==1
                || enterpriseHangupAction.getType()==2 || enterpriseHangupAction.getType()==3
                || enterpriseHangupAction.getType()==4 || enterpriseHangupAction.getType()==5
                || enterpriseHangupAction.getType()==6 || enterpriseHangupAction.getType()==7
                || enterpriseHangupAction.getType()==8 || enterpriseHangupAction.getType()==9))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"推送类型：1.呼入来电推送2.呼入呼转响铃推送3.呼入呼转接通推送4.呼入挂机推送" +
                    "5.外呼响铃推送6.外呼呼转响铃推送7.外呼接通推送8.外呼挂机推送9.按键推送");
        if(enterpriseHangupAction.getMethod() == null || !(enterpriseHangupAction.getMethod()==1 || enterpriseHangupAction.getMethod()==2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"推送方法：1.POST 2.GET");
        enterpriseHangupAction.setCreateTime(eha.getCreateTime());
        int success = updateByPrimaryKey(enterpriseHangupAction);

        if(success == 1){
            setRefreshCacheMethod("setCache",enterpriseHangupAction);
            return new ApiResult<>(enterpriseHangupAction);
        }
        logger.error("EnterpriserHangupAction.updateEnterpriseHangupAction error " + enterpriseHangupAction + "success="
        + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public ApiResult<List<EnterpriseHangupAction>> listEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction) {
        if(enterpriseHangupAction.getEnterpriseId() == null || enterpriseHangupAction.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseHangupAction.getType() == null || enterpriseHangupAction.getType() <= 0 )
            return new ApiResult<>(ApiResult.FAIL_RESULT,"推送类型不能为空");

        Condition condition = new Condition(EnterpriseHangupAction.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseHangupAction.getEnterpriseId());
        criteria.andEqualTo("type",enterpriseHangupAction.getType());
        List<EnterpriseHangupAction> enterpriseHangupActionList = selectByCondition(condition);

        if(enterpriseHangupActionList != null && enterpriseHangupActionList.size() > 0)
            return new ApiResult<>(enterpriseHangupActionList);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"获取推送设置列表失败");
    }

    @Override
    public ApiResult<EnterpriseHangupAction> getEnterpriseHangupAction(EnterpriseHangupAction enterpriseHangupAction) {
        if(enterpriseHangupAction.getEnterpriseId() == null || enterpriseHangupAction.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseHangupAction.getId() == null || enterpriseHangupAction.getId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"推送设置id不正确");

        Condition condition = new Condition(EnterpriseHangupAction.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseHangupAction.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseHangupAction.getId());
        List<EnterpriseHangupAction> enterpriseHangupActionList = selectByCondition(condition);

        if(enterpriseHangupActionList != null && enterpriseHangupActionList.size() <= 0)
            return new ApiResult<>(enterpriseHangupAction);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"获取推送信息失败");
    }

    protected String getKey(EnterpriseHangupAction enterpriseHangupAction) {
        return String.format(CacheKey.ENTERPRISE_HANGUP_ACTION_ENTERPRISE_ID_TYPE,enterpriseHangupAction.getEnterpriseId(),enterpriseHangupAction.getType());
    }

    public void setCache(EnterpriseHangupAction enterpriseHangupAction){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseHangupAction),enterpriseHangupAction);
    }

    public void deleteCache(EnterpriseHangupAction enterpriseHangupAction){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseHangupAction));
    }

    private void setRefreshCacheMethod(String methodName,EnterpriseHangupAction enterpriseHangupAction){
        try{
            Method method = this.getClass().getMethod(methodName,EnterpriseHangupAction.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,enterpriseHangupAction);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch (Exception e){
            logger.error("CtiLinkEnterpriseHangupActionServiceImp.setRefreshCacheMethod error refresh cache fail, class=" + this.getClass().getName(),e);
        }
    }
}
