package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.Result;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseTime;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseTimeService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

/**
 * @author huangbin //
 * @date 2016-04-18 //
 */

@Service
public class EnterpriseTimeServiceImp extends BaseService<EnterpriseTime> implements CtiLinkEnterpriseTimeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<EnterpriseTime> createEnterpriseTime(EnterpriseTime enterpriseTime) {
        if( ! entityDao.validateEntity(enterpriseTime.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");

        ApiResult<EnterpriseTime> result = validateEnterpriseTime(enterpriseTime);
        if(result != null)
            return result;

        int success = insertSelective(enterpriseTime);
        if(success==1) {
            setRefreshCacheMethod("setCache",enterpriseTime);
            return new ApiResult<>(ApiResult.SUCCESS_RESULT, ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("EnterpriseTimeServiceImp.createEnterpriseTime error " + enterpriseTime + "success=" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public ApiResult deleteEnterpriseTime(EnterpriseTime enterpriseTime) {
        if( ! entityDao.validateEntity(enterpriseTime.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseTime.getId()==null || enterpriseTime.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"时间条件id不能为空");

        Condition condition = new Condition(EnterpriseTime.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseTime.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseTime.getId());

        int success = deleteByCondition(condition);
        if(success==1) {
            setRefreshCacheMethod("deleteCache",enterpriseTime);
            return new ApiResult(ApiResult.SUCCESS_RESULT, ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("EnterpriseTimeServiceImp.deleteEnterpriseTime error " + enterpriseTime + "success" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult<EnterpriseTime> updateEnterpriseTime(EnterpriseTime enterpriseTime) {
        if( ! entityDao.validateEntity(enterpriseTime.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");

        ApiResult<EnterpriseTime> result = validateEnterpriseTime(enterpriseTime);
        if(result != null)
            return result;

        EnterpriseTime enterpriseTime1 = selectByPrimaryKey(enterpriseTime.getId());
        if(enterpriseTime1.getEnterpriseId() != enterpriseTime.getEnterpriseId())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号和id号不匹配");
        enterpriseTime.setCreateTime(enterpriseTime1.getCreateTime());

        int success = updateByPrimaryKey(enterpriseTime);
        if(success==1) {
            setRefreshCacheMethod("setCache",enterpriseTime);
            return new ApiResult(ApiResult.SUCCESS_RESULT, ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("EnterpriseTimeServiceImp.updateEnterpriseTime error " + enterpriseTime + "success" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public ApiResult<List<EnterpriseTime>> listEnterpriseTime(EnterpriseTime enterpriseTime) {
        if( ! entityDao.validateEntity(enterpriseTime.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");

        Condition condition = new Condition(EnterpriseTime.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseTime.getEnterpriseId());
        List<EnterpriseTime> enterpriseTimeList = selectByCondition(condition);

        return new ApiResult(enterpriseTimeList);
    }

    protected String getKey(EnterpriseTime enterpriseTime) {
        return String.format(CacheKey.ENTERPRISE_TIME_ENTERPRISE_ID_ID,enterpriseTime.getEnterpriseId(),enterpriseTime.getId());
    }

    public void setCache(EnterpriseTime enterpriseTime){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseTime),enterpriseTime);
    }

    public void deleteCache(EnterpriseTime enterpriseTime){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseTime));
    }

    private void setRefreshCacheMethod(String methodName,EnterpriseTime enterpriseTime){
        try {
            Method method = this.getClass().getMethod(methodName, EnterpriseTime.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,enterpriseTime);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch (Exception e){
            logger.error("EnterpriseTimeServiceImp.setRefreshCacheMethod error cache refresh fail" + "class = "
                    + this.getClass().getName(), e);
        }
    }

    private <T> ApiResult<T> validateEnterpriseTime(EnterpriseTime enterpriseTime){
        if(StringUtils.isEmpty(enterpriseTime.getName()))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"时间条件名称不能为空");
        if(enterpriseTime.getType() == null || !(enterpriseTime.getType()==1 || enterpriseTime.getType()==2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"时间类型为1按星期，2按特殊日期");
        if(enterpriseTime.getType()==1){
            enterpriseTime.setFromDay("");
            enterpriseTime.setToDay("");
            if(StringUtils.isEmpty(enterpriseTime.getDayOfWeek()))
                return new ApiResult(ApiResult.FAIL_RESULT,"请选择星期几");

            Pattern pattern = Pattern.compile("([1-7],){0,6}[1-7]");
            Matcher matcher = pattern.matcher(enterpriseTime.getDayOfWeek().trim());
            if( ! matcher.matches())
                return new ApiResult<>(ApiResult.FAIL_RESULT,"星期格式不正确");

            String dayOfWeek = enterpriseTime.getDayOfWeek().trim();
            String[] dow = dayOfWeek.split(",");
            for(int i=0;i<dow.length;i++){
                int week = Integer.parseInt(dow[i]);
                System.out.println(week);
                if(week<1 || week>7)
                    return new ApiResult(ApiResult.FAIL_RESULT,"星期几对应的取值范围是1-7");
            }
        }
        if(enterpriseTime.getType()==2){
            enterpriseTime.setDayOfWeek("");
            if(StringUtils.isEmpty(enterpriseTime.getFromDay()))
                return new ApiResult(ApiResult.FAIL_RESULT,"起始日期不能为空");
            if(StringUtils.isEmpty(enterpriseTime.getToDay()))
                return new ApiResult(ApiResult.FAIL_RESULT,"截止日期不能为空");
            String start = enterpriseTime.getFromDay();
            String end = enterpriseTime.getToDay();

            Pattern day = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}");
            Matcher matcher1 = day.matcher(enterpriseTime.getFromDay().trim());
            Matcher matcher2 = day.matcher(enterpriseTime.getToDay().trim());
            if(!(matcher1.matches() && matcher2.matches()))
                return new ApiResult<>(ApiResult.FAIL_RESULT,"日期格式不正确");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date fromDay = sdf.parse(start);
                Date toDay = sdf.parse(end);
                if(fromDay.compareTo(toDay)>0)
                    return new ApiResult(ApiResult.FAIL_RESULT,"起始日期不能大于截止日期");

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(enterpriseTime.getStartTime().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"开始时间不能为空");
        if(enterpriseTime.getEndTime().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"结束时间不能为空");

        Pattern time = Pattern.compile("[0-9]{1,2}:[0-9]{1,2}");
        Matcher t1 = time.matcher(enterpriseTime.getStartTime().trim());
        Matcher t2 = time.matcher(enterpriseTime.getEndTime().trim());
        if( ! (t1.matches() && t2.matches()))
            return  new ApiResult<>(ApiResult.FAIL_RESULT,"时间格式不正确");

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        try {
            Date starttime = sdf.parse(enterpriseTime.getStartTime().trim());
            Date endTime = sdf.parse(enterpriseTime.getEndTime().trim());
            if(starttime.compareTo(endTime)>0)
                return new ApiResult(ApiResult.FAIL_RESULT,"开始时间不能大于结束时间");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(enterpriseTime.getPriority()==null || enterpriseTime.getPriority()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"优先级从1开始");

        return null;
    }

}