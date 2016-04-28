package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.model.EnterpriseArea;
import com.tinet.ctilink.conf.service.v1.EnterpriseAreaService;
import com.tinet.ctilink.service.BaseService;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

/**
 * @author huangbin //
 * @date 2016/4/18. //
 */

@Service
public class EnterpriseAreaServiceImp extends BaseService<EnterpriseArea> implements EnterpriseAreaService {

    @Override
    public ApiResult createEnterpriseArea(EnterpriseArea enterpriseArea) {
        if(enterpriseArea.getEnterpriseId()==null || enterpriseArea.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseArea.getGroupId()==null || enterpriseArea.getGroupId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组号不正确");
        if(enterpriseArea.getAreaCode().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"地区区号不能为空");
        if(enterpriseArea.getProvince().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"省份不能为空");
        if(enterpriseArea.getCity().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"城市不能为空");
        enterpriseArea.setCreateTime(new Date());

        int success = insertSelective(enterpriseArea);
        if(success==1)
            return new ApiResult(enterpriseArea);
        return new ApiResult(ApiResult.FAIL_RESULT,"增加失败");
    }

    @Override
    public ApiResult deleteEnterpriseArea(EnterpriseArea enterpriseArea) {
        if(enterpriseArea.getEnterpriseId()==null || enterpriseArea.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseArea.getId()==null || enterpriseArea.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组地区id不能为空");

        Condition condition = new Condition(EnterpriseArea.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseArea.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseArea.getId());
        int success = deleteByCondition(enterpriseArea);
        if(success==1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");

    }

    @Override
    public ApiResult getListEnterpriseArea(EnterpriseArea enterpriseArea) {
        if(enterpriseArea.getEnterpriseId()==null || enterpriseArea.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业id不能为空");
        if(enterpriseArea.getGroupId()==null || enterpriseArea.getGroupId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组id不能为空");

        Condition condition = new Condition(EnterpriseArea.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseArea.getEnterpriseId());
        criteria.andEqualTo("groupId",enterpriseArea.getGroupId());
        List<EnterpriseArea> enterpriseAreaList = selectByCondition(condition);
        if(enterpriseAreaList!=null && enterpriseAreaList.size()>0)
            return new ApiResult(enterpriseAreaList);
        return new ApiResult(ApiResult.FAIL_RESULT,"获取地区列表失败");
    }

}