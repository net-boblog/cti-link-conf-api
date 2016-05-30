package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.RestrictTel;
import com.tinet.ctilink.conf.request.RestrictTelRequest;
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
 * @author huangbin
 * @date 2016/4/28.
 */
@Service
public class RestrictTelServiceImp extends BaseService<RestrictTel> implements CtiLinkRestrictTelService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<RestrictTel> createRestrictTel(RestrictTel restrictTel) {
        if( ! entityDao.validateEntity(restrictTel.getEnterpriseId()))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");

        ApiResult<RestrictTel> result = validateRestrictTel(restrictTel);
        if(result != null)
            return result;

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
        if( ! entityDao.validateEntity(restrictTel.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(restrictTel.getId() == null || restrictTel.getId() <= 0 )
            return new ApiResult(ApiResult.FAIL_RESULT,"黑白名单id不正确");

        Condition condition = new Condition(RestrictTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",restrictTel.getEnterpriseId());
        criteria.andEqualTo("id",restrictTel.getId());

        List<RestrictTel> restrictTelListt = selectByCondition(condition);
        RestrictTel restrictTel1 = null;
        if(restrictTelListt != null && restrictTelListt.size() > 0)
            restrictTel1 = restrictTelListt.get(0);

        int success = deleteByCondition(condition);
        if(success ==1 ){
            setRefreshCacheMethod("deleteCache",restrictTel1);
            return new ApiResult(ApiResult.SUCCESS_RESULT,"删除成功");
        }
        logger.error("RestrictTelServiceImp.deleteRestrictTel error " + restrictTel + "success=" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult<PageInfo<RestrictTel>> listRestrictTel(RestrictTelRequest restrictTelRequest) {
        if( ! entityDao.validateEntity(restrictTelRequest.getEnterpriseId()))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");

        if(restrictTelRequest.getType() == null || !(restrictTelRequest.getType() == 1 || restrictTelRequest.getType() == 2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"呼叫类型为：1.呼入 2.外呼");

        Condition condition = new Condition(RestrictTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", restrictTelRequest.getEnterpriseId());
        criteria.andEqualTo("type", restrictTelRequest.getType());

        //可选项
        if (restrictTelRequest.getRestrictType() != null) {
            if (!(restrictTelRequest.getRestrictType() == 1 || restrictTelRequest.getRestrictType() == 2))
                return new ApiResult<>(ApiResult.FAIL_RESULT,"呼叫类型为：1.呼入 2.外呼");
            criteria.andEqualTo("restrictType", restrictTelRequest.getRestrictType());
        }

        //可选项
        if(restrictTelRequest.getTel() != null ) {
            Pattern pattern1 = Pattern.compile(Const.TEL_VALIDATION);
            Matcher matcher1 = pattern1.matcher(restrictTelRequest.getTel().trim());
            Pattern pattern2 = Pattern.compile(Const.AREA_CODE_VALIDATION);
            Matcher matcher2 = pattern2.matcher(restrictTelRequest.getTel().trim());
            if ( ! (matcher1.matches() || matcher2.matches()))
                return new ApiResult<>(ApiResult.FAIL_RESULT,"号码不正确");
            criteria.andEqualTo("tel", restrictTelRequest.getTel());
        }

        if(restrictTelRequest.getTel() != null )
            criteria.andEqualTo("tel", restrictTelRequest.getTel());
        if(restrictTelRequest.getOffset() < 0 )
            return  new ApiResult<>(ApiResult.FAIL_RESULT,"偏移位置错误");
        if(restrictTelRequest.getLimit() > 500)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"查询条数不能大于500");

        PageHelper.startPage(restrictTelRequest.getOffset(), restrictTelRequest.getLimit());
        List<RestrictTel> restrictTelList = selectByCondition(condition);
        if (restrictTelList ==null || restrictTelList.size() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"获取列表失败");
        PageInfo<RestrictTel> page = new PageInfo<>(restrictTelList);
       return  new ApiResult<>(page);

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
            logger.error("RestrictTelServiceImp.setRefreshCacheMethod error refresh cache fail, class=" + this.getClass().getName() );
        }
    }

    private <T> ApiResult<T>  validateRestrictTel(RestrictTel restrictTel){
        if(restrictTel.getRestrictType() == null || !(restrictTel.getRestrictType() ==1 || restrictTel.getRestrictType()==2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"黑白名单类型为：1.黑名单 2.白名单");

        if(restrictTel.getType() == null || !(restrictTel.getType() == 1 || restrictTel.getType()==2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"呼叫类型为： 1.呼入 2.外呼");

        if(restrictTel.getTel().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"电话不能为空");

        if(restrictTel.getTelType() == null || !(restrictTel.getTelType() ==1 || restrictTel.getTelType() == 2 || restrictTel.getTelType() == 3))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"电话号码类型不能为空");

        if(restrictTel.getTelType() == 1){
            String tel = restrictTel.getTel().trim();
            Pattern pattern = Pattern.compile(Const.TEL_VALIDATION);
            Matcher matcher = pattern.matcher(tel);
            if ( ! matcher.matches() )
                return new ApiResult<>(ApiResult.FAIL_RESULT,"电话号码不正确");
        }
        if(restrictTel.getTelType() == 2 ){
            String tel = restrictTel.getTel().trim();
            Pattern pattern = Pattern.compile(Const.AREA_CODE_VALIDATION);
            Matcher matcher = pattern.matcher(tel);
            if(! matcher.matches())
                return new ApiResult<>(ApiResult.FAIL_RESULT,"地区编码不正确");
        }
        if(restrictTel.getTelType() == 3){
            restrictTel.setTel("unknown_number");
        }

        return null;
    }
}
