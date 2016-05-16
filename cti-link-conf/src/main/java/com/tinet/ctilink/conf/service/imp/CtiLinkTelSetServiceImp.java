package com.tinet.ctilink.conf.service.imp;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.CtiLinkApiResult;
import com.tinet.ctilink.conf.dao.TelSetTelDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.TelSet;
import com.tinet.ctilink.conf.model.TelSetTel;
import com.tinet.ctilink.conf.request.CtiLinkTelSetListRequest;
import com.tinet.ctilink.conf.service.v1.CtiLinkTelSetService;
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
 * @date 16/4/12 17:20
 */
@Service
public class CtiLinkTelSetServiceImp extends BaseService<TelSet> implements CtiLinkTelSetService {

    @Autowired
    private TelSetTelDao telSetTelDao;

    @Autowired
    private RedisService redisService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public CtiLinkApiResult createTelSet(TelSet telSet) {
        if (telSet.getEnterpriseId() == null || telSet.getEnterpriseId() <= 0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "企业编号不正确");
        if (telSet.getTsno() == null || telSet.getTsno().trim().length() > 8)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "电话组组号不大于8位");
        if (telSet.getSetName() == null || "".equals(telSet.getSetName().trim()))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "电话组名不能为空");
        if (telSet.getTimeout() == null || telSet.getTimeout() > 600 || telSet.getTimeout() < 5)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "超时时间为5-600");
        if (telSet.getStrategy() == null || !("order".equals(telSet.getStrategy()) || "random".equals(telSet.getStrategy())))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "呼叫策略取值为order或random");
        if (telSet.getIsStop() == null || !(telSet.getIsStop() == 1 || telSet.getIsStop() == 0))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "是否停用取值为1或0");

        int success = insertSelective(telSet);
        if (success == 1) {
            setRefreshCacheMethod("setCache",telSet);
            return new CtiLinkApiResult(CtiLinkApiResult.SUCCESS_RESULT, CtiLinkApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("CtiLinkTelSetServiceImp.createTelSet error "+ telSet +",success = " + success);
        return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "添加失败");
    }

    @Override
    public CtiLinkApiResult deleteTelSet(TelSet telSet) {
        if (telSet.getEnterpriseId() == null || telSet.getEnterpriseId() <= 0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "企业编号不能为空");
        if (telSet.getId() == null || telSet.getId() <= 0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "电话组id不能为空");

        //删除电话组电话
        Condition telCondition = new Condition(TelSetTel.class);
        Condition.Criteria telCriteria = telCondition.createCriteria();
        telCriteria.andEqualTo("enterpriseId",telSet.getEnterpriseId());
        telCriteria.andEqualTo("tsno",telSet.getTsno());
        telCondition.setTableName("cti_link_tel_set_tel");
        telSetTelDao.deleteByCondition(telCondition);

        //删除电话组
        Condition condition = new Condition(TelSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("id", telSet.getId());
        criteria.andEqualTo("enterpriseId", telSet.getEnterpriseId());
        int success = deleteByCondition(condition);

        if (success == 1) {
            setRefreshCacheMethod("deleteCache",telSet);
            return new CtiLinkApiResult<>(CtiLinkApiResult.SUCCESS_RESULT, CtiLinkApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("CtiLinkTelSetServiceImp.deleteTelSet error "+ telSet +",success = "+success);
        return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "失败");
    }

    @Override
    public CtiLinkApiResult updateTelSet(TelSet telSet) {
        if (telSet.getEnterpriseId() == null || telSet.getEnterpriseId() <= 0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "企业编号不能为空");
        if (telSet.getId() == null || telSet.getId() <= 0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "电话组id不能为空");
        if (telSet.getSetName() == null || "".equals(telSet.getSetName().trim()))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "电话组名不能为空");
        if (telSet.getStrategy() == null || !("order".equals(telSet.getStrategy()) || "random".equals(telSet.getStrategy())))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "呼叫策略为order或random");
        if (telSet.getIsStop() == null || !(telSet.getIsStop() == 1 || telSet.getIsStop() == 0))
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "是否停用取值为0或1");
        Date modify = new Date();
        telSet.setModifyTime(modify);
        int success = updateByPrimaryKeySelective(telSet);

        if (success == 1) {
            setRefreshCacheMethod("setCache",telSet);
            return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, CtiLinkApiResult.SUCCESS_DESCRIPTION);
        }
        return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "更新失败");
    }

    @Override
    public CtiLinkApiResult listTelSet(CtiLinkTelSetListRequest ctiLinkTelSetListRequest) {
        if (ctiLinkTelSetListRequest.getEnterpriseId() == null || ctiLinkTelSetListRequest.getEnterpriseId() <= 0)
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "企业编号不能为空");

        Condition condition = new Condition(TelSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", ctiLinkTelSetListRequest.getEnterpriseId());

        //可选项
        if ( ctiLinkTelSetListRequest.getTsno() != null && ! ctiLinkTelSetListRequest.getTsno().isEmpty() )
            criteria.andEqualTo("tsno", ctiLinkTelSetListRequest.getTsno());
        if( ctiLinkTelSetListRequest.getSetName() != null && ! ctiLinkTelSetListRequest.getSetName().isEmpty())
            criteria.andEqualTo("setName", ctiLinkTelSetListRequest.getSetName());

        PageHelper.startPage(ctiLinkTelSetListRequest.getOffset(), ctiLinkTelSetListRequest.getLimit());
        List<TelSet> telSetList = selectByCondition(condition);
        if(telSetList != null && telSetList.size() > 0)
            for (int i=0; i<telSetList.size(); i++)
                System.out.println("------>"+telSetList.get(i));

        PageInfo page = new PageInfo(telSetList);
        return new CtiLinkApiResult(page);
    }

    @Override
    public CtiLinkApiResult getTelSetByIdAndEnterpriseId(TelSet telSet) {
        if (telSet.getId() == null || telSet.getId() < 0) {
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "电话组id不正确");
        }
        if (telSet.getEnterpriseId() == null || telSet.getEnterpriseId() <= 0) {
            return new CtiLinkApiResult(CtiLinkApiResult.FAIL_RESULT, "企业编号不正确");
        }

        Condition condition = new Condition(TelSet.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", telSet.getEnterpriseId());
        criteria.andEqualTo("id", telSet.getId());
        List<TelSet> telSetList = selectByCondition(condition);

        if (telSetList != null && telSetList.size() > 0)
            return new CtiLinkApiResult<>(telSetList.get(0));
        return new CtiLinkApiResult<>(CtiLinkApiResult.FAIL_RESULT, "查询失败");
    }

    protected String getKey(TelSet telSet) {
        return String.format(CacheKey.TEL_SET_ENTERPRISE_ID_TSNO,
                telSet.getEnterpriseId(), telSet.getTsno());
    }

    public void deleteCache(TelSet telSet){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(telSet));
        redisService.delete(Const.REDIS_DB_CONF_INDEX,String.format(CacheKey.TEL_SET_TEL_ENTERPRISE_TSNO,telSet.getEnterpriseId(), telSet.getTsno()));
    }

    public void setCache(TelSet telSet){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(telSet),telSet);

    }

    private void setRefreshCacheMethod(String methodName,TelSet telSet) {
        try {
            Method method = this.getClass().getMethod(methodName, TelSet.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,telSet);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("CtiLinkTelSetServiceImp setRefreshCacheMethod error,refresh cache fail class=" +this.getClass().getName());
        }
    }
}
