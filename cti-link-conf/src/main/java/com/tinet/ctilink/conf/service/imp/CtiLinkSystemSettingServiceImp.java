package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.SystemSetting;
import com.tinet.ctilink.conf.service.v1.CtiLinkSystemSettingService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author huangbin //
 * @date 16/4/21 16:36
 */
@Service
public class CtiLinkSystemSettingServiceImp extends BaseService<SystemSetting> implements CtiLinkSystemSettingService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<SystemSetting> updateSystemSetting(SystemSetting systemSetting) {
        SystemSetting st = selectByPrimaryKey(systemSetting);
        if (st == null)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"id不正确");
        if(systemSetting.getName().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"名称不能为空");
        if(systemSetting.getValue().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"值不能为空");
        if(systemSetting.getProperty().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"属性不能为空");
        systemSetting.setCreateTime(st.getCreateTime());
        int success = updateByPrimaryKey(systemSetting);

        if(success == 1){
            setRefreshCacheMethod("setCache",systemSetting);
            return new ApiResult<>(systemSetting);
        }
        logger.error("CtiLinkSystemSettingServiceImp.updateSystemSetting error " + systemSetting + "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public ApiResult<List<SystemSetting>> listSystemSetting(SystemSetting systemSetting) {
        List<SystemSetting> list = selectAll();
        if(list != null && list.size()>0)
            return new ApiResult<>(list);
        else
            return new ApiResult<>(ApiResult.FAIL_RESULT,"获取平台设置列表错误");
    }

    protected String getKey(SystemSetting systemSetting) {
        return String.format(CacheKey.SYSTEM_SETTING,systemSetting.getName());
    }

    public void setCache(SystemSetting systemSetting){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(systemSetting),systemSetting);
    }

    public void deleteCache(SystemSetting systemSetting){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(systemSetting));
    }

    private void setRefreshCacheMethod(String methodName,SystemSetting systemSetting){
        try {
            Method method = this.getClass().getMethod(methodName,SystemSetting.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,systemSetting);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch (Exception e){
            logger.error("CtiLinkSystemSettingServiceImp.setRefreshCacheMethod error refresh cache fail class = " + this.getClass().getName(),e);
        }
    }
}
