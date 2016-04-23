package com.tinet.ctilink.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.EnterpriseVoicemail;
import org.springframework.beans.factory.InitializingBean;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

/**@author huangbin
 * @date 2016/4/22.
 */

@Service
public class EnterpriseVoicemailServiceImp extends BaseService<EnterpriseVoicemail> implements EnterpriseVoicemailService,InitializingBean {

    @Override
    public ApiResult createEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail) {
        if(enterpriseVoicemail.getEnterpriseId()==null || enterpriseVoicemail.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseVoicemail.getName().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"留言箱名称不能为空");
        if(enterpriseVoicemail.getVno().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"留言箱号不能为空");
        if (!(enterpriseVoicemail.getType()==1 || enterpriseVoicemail.getType()==2 || enterpriseVoicemail.getType()==3))
            return new ApiResult(ApiResult.FAIL_RESULT,"留言箱类型：1 公共留言箱 2 队列私有留言箱 3 坐席私有留言箱" );

        enterpriseVoicemail.setCreateTime(new Date());
        int success = insertSelective(enterpriseVoicemail);
        if(success == 1)
            return new ApiResult(enterpriseVoicemail);
        return new ApiResult(ApiResult.FAIL_RESULT,"新增失败");
    }

    @Override
    public ApiResult deleteEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail) {
        if(enterpriseVoicemail.getEnterpriseId()==null || enterpriseVoicemail.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseVoicemail.getId()==null || enterpriseVoicemail.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"留言箱id不正确");

        Condition condition = new Condition(EnterpriseVoicemail.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseVoicemail.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseVoicemail.getId());
        int success = deleteByCondition(condition);
        if(success == 1)
            return new ApiResult(ApiResult.SUCCESS_RESULT,ApiResult.SUCCESS_DESCRIPTION);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult updateEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail) {
        if(enterpriseVoicemail.getEnterpriseId()==null || enterpriseVoicemail.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseVoicemail.getName().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"留言箱名称不能为空");
        if(enterpriseVoicemail.getVno().isEmpty())
            return new ApiResult(ApiResult.FAIL_RESULT,"留言箱号不能为空");
        if (!(enterpriseVoicemail.getType()==1 || enterpriseVoicemail.getType()==2 || enterpriseVoicemail.getType()==3))
            return new ApiResult(ApiResult.FAIL_RESULT,"留言箱类型：1 公共留言箱 2 队列私有留言箱 3 坐席私有留言箱" );

        EnterpriseVoicemail evm = selectByPrimaryKey(enterpriseVoicemail.getId());
        if(!(enterpriseVoicemail.getEnterpriseId().equals( evm.getEnterpriseId())))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号和留言箱id不匹配");
        enterpriseVoicemail.setCreateTime(evm.getCreateTime());
        int success = updateByPrimaryKey(enterpriseVoicemail);
        if(success == 1)
            return new ApiResult(enterpriseVoicemail);
        return new ApiResult(ApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public ApiResult getListEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail) {
        if(enterpriseVoicemail.getEnterpriseId()==null || enterpriseVoicemail.getEnterpriseId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");

        Condition condition = new Condition(EnterpriseVoicemail.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseVoicemail.getEnterpriseId());
        List<EnterpriseVoicemail> enterpriseVoicemailList = selectByCondition(condition);
        if(enterpriseVoicemailList!=null && enterpriseVoicemailList.size()>0)
            return new ApiResult(enterpriseVoicemailList);
        return new ApiResult(ApiResult.FAIL_RESULT,"获取留言箱列表失败");
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
