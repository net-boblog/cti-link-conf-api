package com.tinet.ctilink.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseAreaGroup;
import org.springframework.beans.factory.InitializingBean;

import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

/**
 * @author huangbin
 * @date 2016/4/18.
 */

@Service
public class EnterpriseAreaGroupServiceImp extends BaseService<EnterpriseAreaGroup> implements EnterpriseAreaGroupService,InitializingBean {

    @Override
    public ApiResult createEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {
        if(enterpriseAreaGroup.getEnterpriseId()==null || enterpriseAreaGroup.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseAreaGroup.getGroupName()==null || "".equals(enterpriseAreaGroup.getGroupName().trim()))
            return new ApiResult(ApiResult.FAIL_RESULT,"地区名称不为空");
        if(enterpriseAreaGroup.getGroupType() != null){
            if(!(enterpriseAreaGroup.getGroupType()==1 || enterpriseAreaGroup.getGroupType()==2))
                return new ApiResult(ApiResult.FAIL_RESULT,"地区组类型：1 地区组，2 其他地区");
        }
        enterpriseAreaGroup.setCreateTime(new Date());
        int success = insertSelective(enterpriseAreaGroup);
        if(success==1)
            return new ApiResult(enterpriseAreaGroup);
        return new ApiResult(ApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public ApiResult deleteEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {
        if(enterpriseAreaGroup.getEnterpriseId()==null || enterpriseAreaGroup.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseAreaGroup.getId()==null || enterpriseAreaGroup.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组id不能为空");

        Condition condition = new Condition(EnterpriseAreaGroup.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseAreaGroup.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseAreaGroup.getId());
        int success = deleteByCondition(condition);
        if(success==1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult updateEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {

        if(enterpriseAreaGroup.getEnterpriseId()==null || enterpriseAreaGroup.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseAreaGroup.getId()==null || enterpriseAreaGroup.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组id不能为空");
        if(enterpriseAreaGroup.getGroupName().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"地区名称不能为空");
        if(enterpriseAreaGroup.getGroupType()!=null && !(enterpriseAreaGroup.getGroupType()==1 || enterpriseAreaGroup.getGroupType()==2))
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组类型为：1 地区组，2 其他地区");

        EnterpriseAreaGroup eag = selectByPrimaryKey(enterpriseAreaGroup);
        if(!(enterpriseAreaGroup.getEnterpriseId().equals(eag.getEnterpriseId())))
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组id和企业编号不匹配");
        enterpriseAreaGroup.setCreateTime(eag.getCreateTime());

        int success = updateByPrimaryKey(enterpriseAreaGroup);
        if(success==1)
            return new ApiResult(enterpriseAreaGroup);
        return new ApiResult(ApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public ApiResult getListEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {
        if(enterpriseAreaGroup.getEnterpriseId()==null || enterpriseAreaGroup.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        Condition condition = new Condition(EnterpriseAreaGroup.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseAreaGroup.getEnterpriseId());
        List<EnterpriseAreaGroup> enterpriseAreaGroupList = selectByCondition(condition);
        if(enterpriseAreaGroupList!=null && enterpriseAreaGroupList.size()>0)
            return new ApiResult(enterpriseAreaGroupList);
        return new ApiResult(ApiResult.FAIL_RESULT,"获取地区组列表失败");
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
