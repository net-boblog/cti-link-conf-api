package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.TelSetTel;
import com.tinet.ctilink.conf.service.v1.TelSetTelService;
import com.tinet.ctilink.service.BaseService;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;
/**
 * @author huangbin
 * @date 16/4/14.
 */

@Service
public class TelSetTelServiceImp extends BaseService<TelSetTel> implements TelSetTelService {
    @Override
    public ApiResult createTelSetTel(TelSetTel telSetTel) {
        if(telSetTel.getEnterpriseId()==null || telSetTel.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSetTel.getId()==null || telSetTel.getSetId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"电话组id不能为空");
        if(telSetTel.getTel()==null || "".equals(telSetTel.getTel().trim()))
            return new ApiResult(ApiResult.FAIL_RESULT,"电话不能为控");
        if(telSetTel.getTimeout()==null || telSetTel.getTimeout()>60 || telSetTel.getTimeout()<5)
            return new ApiResult(ApiResult.FAIL_RESULT,"超时时间为5-60秒");
        if(telSetTel.getPriority()==null || telSetTel.getPriority()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"优先级不能为空");

        int success = insertSelective(telSetTel);
        if(success==1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,"成功");
        return new ApiResult(ApiResult.FAIL_RESULT,"失败");
    }

    @Override
    public ApiResult deleteTelSetTel(TelSetTel telSetTel) {
        if(telSetTel.getEnterpriseId()==null || telSetTel.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSetTel.getId()==null || telSetTel.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"电话组电话id不能为空");

        Condition condition = new Condition(TelSetTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("id",telSetTel.getId());
        criteria.andEqualTo("enterpriseId",telSetTel.getEnterpriseId());
        int success = deleteByCondition(condition);
        if(success==1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,"成功");
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult updateTelSetTel(TelSetTel telSetTel) {
        if(telSetTel.getEnterpriseId()==null || telSetTel.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSetTel.getId()==null || telSetTel.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"电话组电话id不能为空");
        if(telSetTel.getTel()==null || "".equals(telSetTel.getTel().trim()))
            return new ApiResult(ApiResult.FAIL_RESULT,"电话不能为空");
        if(telSetTel.getTimeout()==null || telSetTel.getTimeout()>60 || telSetTel.getTimeout()<5)
            return new ApiResult(ApiResult.FAIL_RESULT,"超时时间为5-60秒");
        if(telSetTel.getPriority()==null || telSetTel.getPriority()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"优先级不能为空");

        int success = updateByPrimaryKeySelective(telSetTel);
        if(success==1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,"成功");
        return new ApiResult(ApiResult.FAIL_RESULT,"修改失败");
    }

    @Override
    public ApiResult getTelSetTels(TelSetTel telSetTel) {
        if(telSetTel.getEnterpriseId()==null || telSetTel.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSetTel.getId()==null || telSetTel.getSetId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"电话组id不能为空");

        Condition condition = new Condition(TelSetTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", telSetTel.getEnterpriseId());
        criteria.andEqualTo("setId", telSetTel.getSetId());
        List<TelSetTel> telSetTelsList = selectByCondition(condition);
        if(telSetTelsList!=null && telSetTelsList.size()>0)
            return new ApiResult(telSetTelsList);
        return new ApiResult(ApiResult.FAIL_RESULT,"失败");
    }

}