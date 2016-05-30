package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.dao.EnterpriseAreaDao;
import com.tinet.ctilink.conf.dao.EntityDao;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author huangbin
 * @date 2016/4/18.
 */

@Service
public class EnterpriseAreaGroupServiceImp extends BaseService<EnterpriseAreaGroup> implements CtiLinkEnterpriseAreaGroupService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private EnterpriseAreaDao enterpriseAreaDao;

    @Override
    public ApiResult<EnterpriseAreaGroup> createEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {
        if( ! entityDao.validateEntity(enterpriseAreaGroup.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");

        ApiResult<EnterpriseAreaGroup> result = validateEnterpriseAreaGroup(enterpriseAreaGroup);
        if(result != null)
            return result;

        enterpriseAreaGroup.setCreateTime(new Date());

        int success = insertSelective(enterpriseAreaGroup);
        if(success==1) {
            setRefreshCacheMethod("setCache",enterpriseAreaGroup);
            return new ApiResult<>(enterpriseAreaGroup);
        }
        logger.error("EnterpriseAreaGroupServiceImp.createEnterpriseAreaGroup error " + enterpriseAreaGroup + "success=" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public ApiResult deleteEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {
        if( ! entityDao.validateEntity(enterpriseAreaGroup.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseAreaGroup.getId()==null || enterpriseAreaGroup.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组id不能为空");

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

        EnterpriseAreaGroup enterpriseAreaGroup1 = null;
        List<EnterpriseAreaGroup> enterpriseAreaGroupList = selectByCondition(condition);
        if (enterpriseAreaGroupList != null && enterpriseAreaGroupList.size() > 0)
            enterpriseAreaGroup1 = enterpriseAreaGroupList.get(0);

        int success = deleteByCondition(condition);
        if(success==1) {
            setRefreshCacheMethod("deleteCache",enterpriseAreaGroup1);
            return new ApiResult<>(ApiResult.SUCCESS_RESULT, ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("EnterpriseAreaGroupServiceImp.deleteEnterpriseAreaGroup error " + enterpriseAreaGroup + "success=" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult<EnterpriseAreaGroup> updateEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {

        if( ! entityDao.validateEntity(enterpriseAreaGroup.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");
        if(enterpriseAreaGroup.getId()==null || enterpriseAreaGroup.getId()<=0)
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组id不能为空");

        ApiResult<EnterpriseAreaGroup> result = validateEnterpriseAreaGroup(enterpriseAreaGroup);
        if(result != null)
            return result;

        EnterpriseAreaGroup eag = selectByPrimaryKey(enterpriseAreaGroup);
        if(!(enterpriseAreaGroup.getEnterpriseId().equals(eag.getEnterpriseId())))
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组id和企业编号不匹配");
        enterpriseAreaGroup.setCreateTime(eag.getCreateTime());

        int success = updateByPrimaryKey(enterpriseAreaGroup);
        if(success==1) {
            setRefreshCacheMethod("setCache",enterpriseAreaGroup);
            return new ApiResult<>(enterpriseAreaGroup);
        }
        logger.error("EnterpriseAreaGroup.updateEnterpriseAreaGroup error " + enterpriseAreaGroup + "success=" +success );
        return new ApiResult(ApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public ApiResult<List<EnterpriseAreaGroup>> listEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup) {
        if( ! entityDao.validateEntity(enterpriseAreaGroup.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不能为空");

        Condition condition = new Condition(EnterpriseAreaGroup.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseAreaGroup.getEnterpriseId());

        List<EnterpriseAreaGroup> enterpriseAreaGroupList = selectByCondition(condition);
        if(enterpriseAreaGroupList!=null && enterpriseAreaGroupList.size()>0)
            return new ApiResult<>(enterpriseAreaGroupList);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"获取地区组列表失败");
    }

    protected String getKey(EnterpriseAreaGroup enterpriseAreaGroup) {
        return String.format(CacheKey.ENTERPRISE_AREA_GROUP_ENTERPRISE_ID_ID,enterpriseAreaGroup.getEnterpriseId(),enterpriseAreaGroup.getId());
    }

    public void setCache(EnterpriseAreaGroup enterpriseAreaGroup){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseAreaGroup),enterpriseAreaGroup);
    }

    public void deleteCache(EnterpriseAreaGroup enterpriseAreaGroup){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseAreaGroup));
        Set<String> keys = redisService.scan(Const.REDIS_DB_CONF_INDEX,String.format(CacheKey.ENTERPRISE_AREA_ENTERPRISE_ID_GROUP_ID_AREA_CODE,enterpriseAreaGroup.getEnterpriseId(),enterpriseAreaGroup.getId(),"*"));
        redisService.delete(Const.REDIS_DB_CONF_INDEX,keys);
    }

    private void setRefreshCacheMethod(String methodName,EnterpriseAreaGroup enterpriseAreaGroup){
        try {
            Method method = this.getClass().getMethod(methodName, EnterpriseAreaGroup.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,enterpriseAreaGroup);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("EnterpriseAreaGroupServiceImp setRefreshCacheMethod error, refresh cache fail, class="
            +this.getClass().getName());
        }
        }

    private <T> ApiResult<T> validateEnterpriseAreaGroup(EnterpriseAreaGroup enterpriseAreaGroup){
        if(StringUtils.isEmpty(enterpriseAreaGroup.getGroupName()))
            return new ApiResult(ApiResult.FAIL_RESULT,"地区名称不为空");
        if(enterpriseAreaGroup.getGroupType() == null || !(enterpriseAreaGroup.getGroupType()==1 || enterpriseAreaGroup.getGroupType()==2)){
            return new ApiResult(ApiResult.FAIL_RESULT,"地区组类型：1 地区组，2 其他地区");
        }

        return null;
    }

}