package com.tinet.ctilink.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseTime;
import org.springframework.beans.factory.InitializingBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tk.mybatis.mapper.entity.Condition;

/**
 * @author huangbin //
 * @date 2016-04-18 //
 */

@Service
public class EnterpriseTimeServiceImp extends BaseService<EnterpriseTime> implements EnterpriseTimeService,InitializingBean {
    @Override
    public ApiResult createEnterpriseTime(EnterpriseTime enterpriseTime) {
        if(enterpriseTime.getEnterpriseId()==null || enterpriseTime.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseTime.getName().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"时间条件名称不能为空");
        if(enterpriseTime.getType()==null || !(enterpriseTime.getType()==1 || enterpriseTime.getType()==2))
            return new ApiResult(ApiResult.FAIL_RESULT,"时间类型为1按星期，2按特殊日期");
        if(enterpriseTime.getType()==1){
            enterpriseTime.setFromDay("");
            enterpriseTime.setToDay("");
            if(enterpriseTime.getDayOfWeek().isEmpty())
                return new ApiResult(ApiResult.FAIL_RESULT,"请选择星期几");
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
            if(enterpriseTime.getFromDay().isEmpty())
                return new ApiResult(ApiResult.FAIL_RESULT,"起始日期不能为空");
            if(enterpriseTime.getToDay().isEmpty())
                return new ApiResult(ApiResult.FAIL_RESULT,"截止日期不能为空");
            String start = enterpriseTime.getFromDay();
            String end = enterpriseTime.getToDay();
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
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        try {
            Date starttime = sdf.parse(enterpriseTime.getStartTime());
            Date endTime = sdf.parse(enterpriseTime.getEndTime());
            if(starttime.compareTo(endTime)>0)
                return new ApiResult(ApiResult.FAIL_RESULT,"开始时间不能大于结束时间");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(enterpriseTime.getPriority()==null || enterpriseTime.getPriority()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"优先级不能为空");

        int success = insertSelective(enterpriseTime);
        if(success==1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        return new ApiResult(ApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public ApiResult deleteEnterpriseTime(EnterpriseTime enterpriseTime) {
        if(enterpriseTime.getEnterpriseId()==null || enterpriseTime.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseTime.getId()==null || enterpriseTime.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"时间条件id不能为空");

        Condition condition = new Condition(EnterpriseTime.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseTime.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseTime.getId());
        int success = deleteByCondition(condition);
        if(success==1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult updateEnterpriseTime(EnterpriseTime enterpriseTime) {
        if(enterpriseTime.getEnterpriseId()==null || enterpriseTime.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseTime.getId()==null || enterpriseTime.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"时间条件id不正确");
        EnterpriseTime et = selectByPrimaryKey(enterpriseTime);
        if(!(enterpriseTime.getEnterpriseId().equals(et.getEnterpriseId())))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号和时间条件id不匹配");
        if(enterpriseTime.getName().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"时间条件名称不能为空");

        int type = 0;
        type= et.getType();
        if(type==0){
            return new ApiResult(ApiResult.FAIL_RESULT,"不存在该id的选项");
        }
        if(type==1){
            enterpriseTime.setFromDay("");
            enterpriseTime.setToDay("");
            if(enterpriseTime.getDayOfWeek().isEmpty())
                return new ApiResult(ApiResult.FAIL_RESULT,"请选择星期几");
            String dayOfWeek = enterpriseTime.getDayOfWeek().trim();
            String[] dow = dayOfWeek.split(",");
            for(int i=0;i<dow.length;i++){
                int week = Integer.parseInt(dow[i]);
                if(week<1 || week>7)
                    return new ApiResult(ApiResult.FAIL_RESULT,"星期几对应的取值范围是1-7");
            }
        }
        if(type==2){
            enterpriseTime.setDayOfWeek("");
            if(enterpriseTime.getFromDay().isEmpty() || enterpriseTime.getToDay().isEmpty()){
                return new ApiResult(ApiResult.FAIL_RESULT,"请选择起始日期和结束日期");
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(enterpriseTime.getFromDay());
                Date end = sdf.parse(enterpriseTime.getToDay());
                if (start.compareTo(end)>0)
                    return new ApiResult(ApiResult.FAIL_RESULT,"起始日期不能大于结束日期");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(enterpriseTime.getStartTime().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"请选择开始时间");
        if(enterpriseTime.getEndTime().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"请选择结束时间");
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            Date startTime = sdf.parse(enterpriseTime.getStartTime());
            Date endTime = sdf.parse(enterpriseTime.getEndTime());
            if(startTime.compareTo(endTime) > 0)
                return new ApiResult(ApiResult.FAIL_RESULT,"开始时间不能大于结束时间");
        }catch (Exception e){
            e.printStackTrace();
        }

        if(enterpriseTime.getPriority()==null || enterpriseTime.getPriority()<1)
            return new ApiResult(ApiResult.FAIL_RESULT,"请选择优先级");

        int success = updateByPrimaryKeySelective(enterpriseTime);
        if(success==1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        return new ApiResult(ApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public ApiResult getListEnterpriseTime(EnterpriseTime enterpriseTime) {
        if(enterpriseTime.getEnterpriseId()==null || enterpriseTime.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        Condition condition = new Condition(EnterpriseTime.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseTime.getEnterpriseId());
        List<EnterpriseTime> enterpriseTimeList = selectByCondition(condition);
        return new ApiResult(enterpriseTimeList);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
