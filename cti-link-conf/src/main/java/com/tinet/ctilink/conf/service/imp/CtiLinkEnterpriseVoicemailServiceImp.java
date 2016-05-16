package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseVoicemail;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseVoicemailService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**@author huangbin
 * @date 2016/4/22.
 */

@Service
public class CtiLinkEnterpriseVoicemailServiceImp extends BaseService<EnterpriseVoicemail> implements CtiLinkEnterpriseVoicemailService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Override
    public CtiLinkApiResult<EnterpriseVoicemail> createEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail) {
        if(enterpriseVoicemail.getEnterpriseId()==null || enterpriseVoicemail.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseVoicemail.getName().isEmpty())
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"留言箱名称不能为空");
        if(enterpriseVoicemail.getVno().isEmpty())
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"留言箱号不能为空");
        if (!(enterpriseVoicemail.getType()==1 || enterpriseVoicemail.getType()==2 || enterpriseVoicemail.getType()==3))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"留言箱类型：1 公共留言箱 2 队列私有留言箱 3 坐席私有留言箱" );

        enterpriseVoicemail.setCreateTime(new Date());
        int success = insertSelective(enterpriseVoicemail);

        if(success == 1) {
            setRefreshCacheMethod("setCache",enterpriseVoicemail);
            return new CtiLinkApiResult(enterpriseVoicemail);
        }
        logger.error("EnterpriseVoiceMailServiceImp.createEnterpriseVoicemail error " + enterpriseVoicemail + "success=" + success);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"新增失败");
    }

    @Override
    public CtiLinkApiResult deleteEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail) {
        if(enterpriseVoicemail.getEnterpriseId()==null || enterpriseVoicemail.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseVoicemail.getId()==null || enterpriseVoicemail.getId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"留言箱id不正确");

        Condition condition = new Condition(EnterpriseVoicemail.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseVoicemail.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseVoicemail.getId());
        int success = deleteByCondition(condition);

        if(success == 1) {
            setRefreshCacheMethod("deleteCache",enterpriseVoicemail);
            return new CtiLinkApiResult(CtiLinkApiResult.SUCCESS_RESULT, CtiLinkApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("CtiLinkEnterpriseVoicemailServiceImp.deleteEnterpriseVoicemail error " + enterpriseVoicemail + "success=" + success);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public CtiLinkApiResult<EnterpriseVoicemail> updateEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail) {
        if(enterpriseVoicemail.getEnterpriseId()==null || enterpriseVoicemail.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseVoicemail.getName().isEmpty())
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"留言箱名称不能为空");
        if(enterpriseVoicemail.getVno().isEmpty())
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"留言箱号不能为空");
        if (!(enterpriseVoicemail.getType()==1 || enterpriseVoicemail.getType()==2 || enterpriseVoicemail.getType()==3))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"留言箱类型：1 公共留言箱 2 队列私有留言箱 3 坐席私有留言箱" );

        EnterpriseVoicemail evm = selectByPrimaryKey(enterpriseVoicemail.getId());
        if(!(enterpriseVoicemail.getEnterpriseId().equals( evm.getEnterpriseId())))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号和留言箱id不匹配");
        enterpriseVoicemail.setCreateTime(evm.getCreateTime());
        int success = updateByPrimaryKey(enterpriseVoicemail);

        if(success == 1) {
            setRefreshCacheMethod("setCache",enterpriseVoicemail);
            return new CtiLinkApiResult(enterpriseVoicemail);
        }
        logger.error("EnterpriseVoiceMailServiceImp.updateEnterpriseVoicemail error " + enterpriseVoicemail + "success=" + success);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public CtiLinkApiResult<List<EnterpriseVoicemail>> listEnterpriseVoicemail(EnterpriseVoicemail enterpriseVoicemail) {
        if(enterpriseVoicemail.getEnterpriseId()==null || enterpriseVoicemail.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不正确");

        Condition condition = new Condition(EnterpriseVoicemail.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseVoicemail.getEnterpriseId());
        List<EnterpriseVoicemail> enterpriseVoicemailList = selectByCondition(condition);

        if(enterpriseVoicemailList!=null && enterpriseVoicemailList.size()>0)
            return new CtiLinkApiResult(enterpriseVoicemailList);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"获取留言箱列表失败");
    }

    protected String getKey(EnterpriseVoicemail enterpriseVoicemail) {
        return String.format(CacheKey.ENTERPRISE_VOICEMAIL_ENTERPRISE_ID_ID,enterpriseVoicemail.getEnterpriseId(),enterpriseVoicemail.getId());
    }

    public void setCache(EnterpriseVoicemail enterpriseVoicemail){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseVoicemail),enterpriseVoicemail);
    }

    public void deleteCache(EnterpriseVoicemail enterpriseVoicemail){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseVoicemail));
    }

    public void setRefreshCacheMethod(String methodName,EnterpriseVoicemail enterpriseVoicemail){
        try {
            Method method = this.getClass().getMethod(methodName, EnterpriseVoicemail.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,enterpriseVoicemail);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("EnterpriseVoiceMailServiceImp.setRefreshCacheMethod error cache refresh fail class = " + this.getClass().getName() ,e );
        }
    }

}