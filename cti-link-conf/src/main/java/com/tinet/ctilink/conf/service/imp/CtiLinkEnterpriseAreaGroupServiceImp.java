package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.dao.EnterpriseAreaDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseArea;
import com.tinet.ctilink.conf.model.EnterpriseAreaGroup;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseAreaGroupService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author huangbin
 * @date 2016/4/18.
 */

@Service
public class CtiLinkEnterpriseAreaGroupServiceImp extends BaseService<EnterpriseAreaGroup> implements CtiLinkEnterpriseAreaGroupService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Autowired
    private EnterpriseAreaDao enterpriseAreaDao;

    @Override
    public CtiLinkApiResult<EnterpriseAreaGroup> createEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {
        if(enterpriseAreaGroup.getEnterpriseId()==null || enterpriseAreaGroup.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseAreaGroup.getGroupName()==null || "".equals(enterpriseAreaGroup.getGroupName().trim()))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区名称不为空");
        if(enterpriseAreaGroup.getGroupType() != null){
            if(!(enterpriseAreaGroup.getGroupType()==1 || enterpriseAreaGroup.getGroupType()==2))
                return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区组类型：1 地区组，2 其他地区");
        }
        enterpriseAreaGroup.setCreateTime(new Date());
        int success = insertSelective(enterpriseAreaGroup);

        if(success==1) {
            setRefreshCacheMethod("setCache",enterpriseAreaGroup);
            return new CtiLinkApiResult<>(enterpriseAreaGroup);
        }
        logger.error("CtiLinkEnterpriseAreaGroupServiceImp.createEnterpriseAreaGroup error " + enterpriseAreaGroup + "success=" + success);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public CtiLinkApiResult deleteEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {
        if(enterpriseAreaGroup.getEnterpriseId()==null || enterpriseAreaGroup.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseAreaGroup.getId()==null || enterpriseAreaGroup.getId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区组id不能为空");

        //删除地区组地区
        Condition areaCondition = new Condition(EnterpriseArea.class);
        Condition.Criteria areaCriteria = areaCondition.createCriteria();
        areaCriteria.andEqualTo("enterpriseId",enterpriseAreaGroup.getEnterpriseId());
        areaCriteria.andEqualTo("groupId",enterpriseAreaGroup.getId());
        areaCondition.setTableName("cti_link_enterprise_area");
        enterpriseAreaDao.deleteByCondition(areaCondition);

        //删除地区组
        Condition condition = new Condition(EnterpriseAreaGroup.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseAreaGroup.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseAreaGroup.getId());
        int success = deleteByCondition(condition);

        if(success==1) {
            setRefreshCacheMethod("deleteCache",enterpriseAreaGroup);
            return new CtiLinkApiResult<>(CtiLinkApiResult.SUCCESS_RESULT, CtiLinkApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("CtiLinkEnterpriseAreaGroupServiceImp.deleteEnterpriseAreaGroup error " + enterpriseAreaGroup + "success=" + success);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public CtiLinkApiResult<EnterpriseAreaGroup> updateEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {

        if(enterpriseAreaGroup.getEnterpriseId()==null || enterpriseAreaGroup.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseAreaGroup.getId()==null || enterpriseAreaGroup.getId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区组id不能为空");
        if(enterpriseAreaGroup.getGroupName().isEmpty())
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区名称不能为空");
        if(enterpriseAreaGroup.getGroupType()!=null && !(enterpriseAreaGroup.getGroupType()==1 || enterpriseAreaGroup.getGroupType()==2))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区组类型为：1 地区组，2 其他地区");

        EnterpriseAreaGroup eag = selectByPrimaryKey(enterpriseAreaGroup);
        if(!(enterpriseAreaGroup.getEnterpriseId().equals(eag.getEnterpriseId())))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"地区组id和企业编号不匹配");
        enterpriseAreaGroup.setCreateTime(eag.getCreateTime());
        int success = updateByPrimaryKey(enterpriseAreaGroup);

        if(success==1) {
            setRefreshCacheMethod("setCache",enterpriseAreaGroup);
            return new CtiLinkApiResult<>(enterpriseAreaGroup);
        }
        logger.error("EnterpriseAreaGroup.updateEnterpriseAreaGroup error " + enterpriseAreaGroup + "success=" +success );
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public CtiLinkApiResult<List<EnterpriseAreaGroup>> listEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {
        if(enterpriseAreaGroup.getEnterpriseId()==null || enterpriseAreaGroup.getEnterpriseId()<=0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT,"企业编号不能为空");

        Condition condition = new Condition(EnterpriseAreaGroup.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseAreaGroup.getEnterpriseId());
        List<EnterpriseAreaGroup> enterpriseAreaGroupList = selectByCondition(condition);

        if(enterpriseAreaGroupList!=null && enterpriseAreaGroupList.size()>0)
            return new CtiLinkApiResult<>(enterpriseAreaGroupList);
        return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT,"获取地区组列表失败");
    }

    protected String getKey(EnterpriseAreaGroup enterpriseAreaGroup) {
        return String.format(CacheKey.ENTERPRISE_AREA_GROUP_ENTERPRISE_ID_ID,enterpriseAreaGroup.getEnterpriseId(),enterpriseAreaGroup.getId());
    }

    public void setCache(EnterpriseAreaGroup enterpriseAreaGroup){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseAreaGroup),enterpriseAreaGroup);
    }

    public void deleteCache(EnterpriseAreaGroup enterpriseAreaGroup){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseAreaGroup));
        redisService.delete(Const.REDIS_DB_CONF_INDEX,String.format(CacheKey.ENTERPRISE_AREA_ENTERPRISE_ID_GROUP_ID_AREA_CODE,
                enterpriseAreaGroup.getEnterpriseId(),enterpriseAreaGroup.getId(),"*"));
    }

    private void setRefreshCacheMethod(String methodName,EnterpriseAreaGroup enterpriseAreaGroup){
        try {
            Method method = this.getClass().getMethod(methodName, EnterpriseAreaGroup.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,enterpriseAreaGroup);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("CtiLinkEnterpriseAreaGroupServiceImp setRefreshCacheMethod error, refresh cache fail, class="
            +this.getClass().getName());
        }
        }

}