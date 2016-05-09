package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.TelSet;
import com.tinet.ctilink.conf.request.TelSetListRequest;
import com.tinet.ctilink.conf.service.v1.TelSetService;
import com.tinet.ctilink.service.BaseService;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

/**
 * @author huangbin
 * @date 16/4/12 17:20
 */
@Service
public class TelSetServiceImp extends BaseService<TelSet> implements TelSetService {

    @Override
    public ApiResult createTelSet(TelSet telSet) {
        if(telSet.getEnterpriseId()==null || telSet.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(telSet.getTsno()==null || telSet.getTsno().trim().length()>8)
            return new ApiResult(ApiResult.FAIL_RESULT,"电话组组号不大于8位");
        if(telSet.getSetName()==null || "".equals(telSet.getSetName().trim()))
            return new ApiResult(ApiResult.FAIL_RESULT,"电话组名不能为空");
        if(telSet.getTimeout()==null || telSet.getTimeout()>600 || telSet.getTimeout()<5)
            return new ApiResult(ApiResult.FAIL_RESULT,"超时时间为5-600");
        if(telSet.getStrategy()==null || ! ("order".equals(telSet.getStrategy()) || "random".equals(telSet.getStrategy())))
            return new ApiResult(ApiResult.FAIL_RESULT,"呼叫策略取值为order或random");
        if(telSet.getIsStop()==null || !(telSet.getIsStop()==1 || telSet.getIsStop()==0))
            return new ApiResult(ApiResult.FAIL_RESULT,"是否停用取值为1或0");

        int success = insertSelective(telSet);
        if(success==1){
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        }
        return new ApiResult(ApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public ApiResult deleteTelSet(TelSet telSet) {
        if(telSet.getEnterpriseId()==null || telSet.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSet.getId()==null || telSet.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"电话组id不能为空");
        Condition condition = new Condition(TelSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("id",telSet.getId());
        criteria.andEqualTo("enterpriseId",telSet.getEnterpriseId());
        int success = deleteByCondition(condition);
        if(success==1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        return new ApiResult(ApiResult.FAIL_RESULT,"失败");
    }

    @Override
    public ApiResult updateTelSet(TelSet telSet) {
        if(telSet.getEnterpriseId()==null || telSet.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(telSet.getId()==null || telSet.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"电话组id不能为空");
        if(telSet.getSetName()==null || "".equals(telSet.getSetName().trim()))
            return  new ApiResult(ApiResult.FAIL_RESULT,"电话组名不能为空");
        if(telSet.getStrategy()==null || !("order".equals(telSet.getStrategy())||"random".equals(telSet.getStrategy())))
            return new ApiResult(ApiResult.FAIL_RESULT,"呼叫策略为order或random");
        if(telSet.getIsStop()==null || !(telSet.getIsStop()==1||telSet.getIsStop()==0))
            return new ApiResult(ApiResult.FAIL_RESULT,"是否停用取值为0或1");
        Date modify = new Date();
        telSet.setModifyTime(modify);
        int success = updateByPrimaryKeySelective(telSet);
        if(success==1)
            return new ApiResult(ApiResult.FAIL_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        return new ApiResult(ApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public ApiResult<PageInfo<TelSet>> getListTelSets(TelSetListRequest telSetListRequest) {
        if(telSetListRequest.getEnterpriseId()==null || telSetListRequest.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        Condition condition = new Condition(TelSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", telSetListRequest.getEnterpriseId());

        PageHelper.startPage(telSetListRequest.getOffset(),telSetListRequest.getLimit());
        List<TelSet> telSetList = selectByCondition(condition);

        PageInfo page = new PageInfo(telSetList);
        return new ApiResult(page);
    }

    @Override
    public ApiResult getTelSetByIdAndEnterpriseId(TelSet telSet) {
        if (telSet.getId()==null || telSet.getId() < 0) {
            return new ApiResult(ApiResult.FAIL_RESULT, "电话组id不正确");
        }
        if (telSet.getEnterpriseId()==null || telSet.getEnterpriseId() <= 0) {
            return new ApiResult(ApiResult.FAIL_RESULT, "企业编号不正确");
        }

        Condition condition = new Condition(TelSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", telSet.getEnterpriseId());
        criteria.andEqualTo("id", telSet.getId());
        List<TelSet> telSetList = selectByCondition(condition);
        if (telSetList != null && telSetList.size() > 0)
            return new ApiResult(telSetList.get(0));
        return new ApiResult(ApiResult.FAIL_RESULT,"失败");
    }

}
