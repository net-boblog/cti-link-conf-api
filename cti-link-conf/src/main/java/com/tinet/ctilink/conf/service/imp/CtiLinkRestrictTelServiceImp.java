package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.RestrictTel;
import com.tinet.ctilink.conf.request.CtiLinkRestrictTelRequest;
import com.tinet.ctilink.conf.service.v1.CtiLinkRestrictTelService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
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
public class CtiLinkRestrictTelServiceImp extends BaseService<RestrictTel> implements CtiLinkRestrictTelService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Override
    public CtiLinkApiResult<RestrictTel> createRestrictTel(RestrictTel restrictTel) {
        if(restrictTel.getEnterpriseId() == null || restrictTel.getEnterpriseId() <= 0)
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"企业编号不正确");
        if(restrictTel.getRestrictType() == null || !(restrictTel.getRestrictType() ==1 || restrictTel.getRestrictType()==2))
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"黑白名单类型为：1.黑名单 2.白名单");
        if(restrictTel.getRestrictType() == null || !(restrictTel.getType() == 1 || restrictTel.getType()==2))
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"呼叫类型为： 1.呼入 2.外呼");
        if(restrictTel.getTel().isEmpty())
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"电话不能为空");
        if(restrictTel.getTelType() == null || !(restrictTel.getTelType() ==1 || restrictTel.getTelType() == 2 || restrictTel.getTelType() == 3))
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"电话号码类型不能为空");
        if(restrictTel.getTelType() == 1){
            String tel = restrictTel.getTel();
            Pattern pattern = Pattern.compile(Const.TEL_VALIDATION);
            Matcher matcher = pattern.matcher(tel);
            if ( ! matcher.matches() )
                return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"电话号码不正确");
        }
        if(restrictTel.getTelType() == 2 ){
            String tel = restrictTel.getTel();
            Pattern pattern = Pattern.compile(Const.AREA_CODE_VALIDATION);
            Matcher matcher = pattern.matcher(tel);
            if(! matcher.matches())
                return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"地区编码不正确");
        }
        if(restrictTel.getTelType() == 3){

        }
        restrictTel.setCreateTime(new Date());
        int success = insertSelective(restrictTel);

        if(success == 1){
            setRefreshCacheMethod("setCache",restrictTel);
            return new CtiLinkApiResult<>(restrictTel);
        }
        logger.error("CtiLinkRestrictTelServiceImp.createRestrictTel error " + restrictTel + "success=" + success);
        return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public CtiLinkApiResult deleteRestrictTel(RestrictTel restrictTel) {
        if(restrictTel.getEnterpriseId() == null || restrictTel.getEnterpriseId() <= 0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不正确");
        if(restrictTel.getId() == null || restrictTel.getId() <= 0 )
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"黑白名单id不正确");

        Condition condition = new Condition(RestrictTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",restrictTel.getEnterpriseId());
        criteria.andEqualTo("id",restrictTel.getId());
        int success = deleteByCondition(condition);

        if(success ==1 ){
            setRefreshCacheMethod("deleteCache",restrictTel);
            return new CtiLinkApiResult(CtiLinkApiResult.SUCCESS_RESULT,"删除成功");
        }
        logger.error("CtiLinkRestrictTelServiceImp.deleteRestrictTel error " + restrictTel + "success=" + success);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public CtiLinkApiResult<PageInfo> listRestrictTel(CtiLinkRestrictTelRequest ctiLinkRestrictTelRequest) {
        if(ctiLinkRestrictTelRequest.getEnterpriseId() == null || ctiLinkRestrictTelRequest.getEnterpriseId() <= 0)
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"企业编号不正确");
        if(ctiLinkRestrictTelRequest.getType() == null || !(ctiLinkRestrictTelRequest.getType() == 1 || ctiLinkRestrictTelRequest.getType() == 2))
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"呼叫类型为：1.呼入 2.外呼");

        Condition condition = new Condition(RestrictTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", ctiLinkRestrictTelRequest.getEnterpriseId());
        criteria.andEqualTo("type", ctiLinkRestrictTelRequest.getType());

        //可选项
        if (ctiLinkRestrictTelRequest.getRestrictType() != null) {
            if (!(ctiLinkRestrictTelRequest.getRestrictType() == 1 || ctiLinkRestrictTelRequest.getRestrictType() == 2))
                return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"呼叫类型为：1.呼入 2.外呼");
            criteria.andEqualTo("restrictType", ctiLinkRestrictTelRequest.getRestrictType());
        }
        if(ctiLinkRestrictTelRequest.getTel() != null )
            criteria.andEqualTo("tel", ctiLinkRestrictTelRequest.getTel());
        if(ctiLinkRestrictTelRequest.getOffset() < 0 )
            return  new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"偏移位置错误");
        if(ctiLinkRestrictTelRequest.getLimit() > 500)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"查询条数不能大于500");

        PageHelper.startPage(ctiLinkRestrictTelRequest.getOffset(), ctiLinkRestrictTelRequest.getLimit());
        List<RestrictTel> restrictTelList = selectByCondition(condition);
        PageInfo page = new PageInfo(restrictTelList);
       return  new CtiLinkApiResult<>(page);

    }

    protected String getKey(RestrictTel restrictTel) {
        return String.format(CacheKey.RESTRICT_TEL_ENTERPRISE_ID_TYPE_RESTRICT_TYPE_TEL,restrictTel.getEnterpriseId(),
                restrictTel.getType(),restrictTel.getRestrictType(),restrictTel.getTel());
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
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("CtiLinkRestrictTelServiceImp.setRefreshCacheMethod error refresh cache fail, class=" + this.getClass().getName() );
        }
    }
}
