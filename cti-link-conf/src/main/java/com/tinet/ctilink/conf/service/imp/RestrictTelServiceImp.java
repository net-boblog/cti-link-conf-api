package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.RestrictTel;
import com.tinet.ctilink.conf.request.RestrictTelRequest;
import com.tinet.ctilink.conf.service.AbstractService;
import com.tinet.ctilink.conf.service.v1.RestrictTelService;
import com.tinet.ctilink.inc.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nope-J on 2016/4/28.
 */
@Service
public class RestrictTelServiceImp extends AbstractService<RestrictTel> implements RestrictTelService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<RestrictTel> createRestrictTel(RestrictTel restrictTel) {
        if(restrictTel.getEnterpriseId() == null || restrictTel.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(restrictTel.getRestrictType() == null || !(restrictTel.getRestrictType() ==1 || restrictTel.getRestrictType()==2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"黑白名单类型为：1.黑名单 2.白名单");
        if(restrictTel.getRestrictType() == null || !(restrictTel.getType() == 1 || restrictTel.getType()==2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"呼叫类型为： 1.呼入 2.外呼");
        if(restrictTel.getTel().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"电话不能为空");
        if(restrictTel.getTelType() == null || !(restrictTel.getTelType() ==1 || restrictTel.getTelType() == 2 || restrictTel.getTelType() == 3))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"电话号码类型不能为空");
        if(restrictTel.getTelType() == 1){
            String tel = restrictTel.getTel();
            Pattern pattern = Pattern.compile(Const.TEL_VALIDATION);
            Matcher matcher = pattern.matcher(tel);
            if ( ! matcher.matches() )
                return new ApiResult<>(ApiResult.FAIL_RESULT,"电话号码不正确");
        }
        if(restrictTel.getTelType() == 2 ){
            String tel = restrictTel.getTel();
            Pattern pattern = Pattern.compile(Const.AREA_CODE_VALIDATION);
            Matcher matcher = pattern.matcher(tel);
            if(! matcher.matches())
                return new ApiResult<>(ApiResult.FAIL_RESULT,"地区编码不正确");
        }
        if(restrictTel.getTelType() == 3){

        }
        restrictTel.setCreateTime(new Date());
        int success = insertSelective(restrictTel);

        if(success == 1){
            setRefreshCacheMethod("setCache",restrictTel);
            return new ApiResult<>(restrictTel);
        }
        logger.error("RestrictTelServiceImp.createRestrictTel error " + restrictTel + "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public ApiResult deleteRestrictTel(RestrictTel restrictTel) {
        if(restrictTel.getEnterpriseId() == null || restrictTel.getEnterpriseId() <= 0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(restrictTel.getId() == null || restrictTel.getId() <= 0 )
            return new ApiResult(ApiResult.FAIL_RESULT,"黑白名单id不正确");

        Condition condition = new Condition(RestrictTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",restrictTel.getEnterpriseId());
        criteria.andEqualTo("id",restrictTel.getId());
        int success = deleteByCondition(condition);

        if(success ==1 ){
            setRefreshCacheMethod("deleteCache",restrictTel);
            return new ApiResult(ApiResult.SUCCESS_RESULT,"删除成功");
        }
        logger.error("RestrictTelServiceImp.deleteRestrictTel error " + restrictTel + "success=" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult<PageInfo> listRestrictTel(RestrictTelRequest restrictTelRequest) {
        if(restrictTelRequest.getEnterpriseId() == null || restrictTelRequest.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(restrictTelRequest.getType() == null || !(restrictTelRequest.getType() == 1 || restrictTelRequest.getType() == 2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"呼叫类型为：1.呼入 2.外呼");

        Condition condition = new Condition(RestrictTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",restrictTelRequest.getEnterpriseId());
        criteria.andEqualTo("type",restrictTelRequest.getType());

        //可选项
        if (restrictTelRequest.getRestrictType() != null) {
            if (!(restrictTelRequest.getRestrictType() == 1 || restrictTelRequest.getRestrictType() == 2))
                return new ApiResult<>(ApiResult.FAIL_RESULT,"呼叫类型为：1.呼入 2.外呼");
            criteria.andEqualTo("restrictType",restrictTelRequest.getRestrictType());
        }
        if(restrictTelRequest.getTel() != null )
            criteria.andEqualTo("tel",restrictTelRequest.getTel());
        if(restrictTelRequest.getOffset() < 0 )
            return  new ApiResult(ApiResult.FAIL_RESULT,"偏移位置错误");
        if(restrictTelRequest.getLimit() > 500)
            return new ApiResult(ApiResult.FAIL_RESULT,"查询条数不能大于500");

        PageHelper.startPage(restrictTelRequest.getOffset(),restrictTelRequest.getLimit());
        List<RestrictTel> restrictTelList = selectByCondition(condition);
        PageInfo page = new PageInfo(restrictTelList);
       return  new ApiResult<>(page);

    }



    @Override
    protected List<RestrictTel> select(Integer enterpriseId) {
        return null;
    }

    @Override
    protected String getKey(RestrictTel restrictTel) {
        return String.format(CacheKey.RESTRICT_TEL_ENTERPRISE_ID_TYPE_RESTRICT_TYPE_TEL,restrictTel.getEnterpriseId(),
                restrictTel.getType(),restrictTel.getRestrictType(),restrictTel.getTel());
    }

    @Override
    protected String getCleanKeyPrefix() {
        return null;
    }

    @Override
    protected String getRefreshKeyPrefix(Integer enterpriseId) {
        return null;
    }

    public void setCache(RestrictTel restrictTel){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(restrictTel),restrictTel);
    }

    public void deleteCache(RestrictTel restrictTel){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(restrictTel));
    }

    private void setRefreshCacheMethod(String methodName,RestrictTel restrictTel){
        try {
            Method method = this.getClass().getMethod(methodName, RestrictTel.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,restrictTel);
            ProviderFilter.methodThreadLocal.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("RestrictTelServiceImp.setRefreshCacheMethod error refresh cache fail, class=" + this.getClass().getName() );
        }
    }
}
