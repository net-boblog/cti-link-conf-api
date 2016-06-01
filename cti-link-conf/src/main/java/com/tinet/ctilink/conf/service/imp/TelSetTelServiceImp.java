package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.mapper.TelSetMapper;
import com.tinet.ctilink.conf.model.TelSet;
import com.tinet.ctilink.conf.model.TelSetTel;
import com.tinet.ctilink.conf.service.v1.CtiLinkTelSetTelService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huangbin
 * @date 16/4/14.
 */

@Service
public class TelSetTelServiceImp extends BaseService<TelSetTel> implements CtiLinkTelSetTelService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private TelSetMapper telSetMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult createTelSetTel(TelSetTel telSetTel) {
        if (!entityMapper.validateEntity(telSetTel.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT, "企业编号不正确");
        ApiResult<TelSetTel> result = validateTelSetTel(telSetTel);
        if (result != null)
            return result;

        telSetTel.setCreateTime(new Date());

        int success = insertSelective(telSetTel);
        if (success == 1) {
            setRefreshCacheMethod("setCache", telSetTel);
            return new ApiResult<>(ApiResult.SUCCESS_RESULT, ApiResult.SUCCESS_DESCRIPTION);
        }
        logger.error("TelSetTelServiceImp.createTelSetTel error " + telSetTel + "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT, "新增失败");
    }

    @Override
    public ApiResult deleteTelSetTel(TelSetTel telSetTel) {
        if (!entityMapper.validateEntity(telSetTel.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT, "企业编号不正确");
        if (telSetTel.getId() == null || telSetTel.getId() <= 0)
            return new ApiResult(ApiResult.FAIL_RESULT, "电话组电话id不能为空");

        Condition condition = new Condition(TelSetTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("id", telSetTel.getId());
        criteria.andEqualTo("enterpriseId", telSetTel.getEnterpriseId());

        List<TelSetTel> telSetTelList = selectByCondition(condition);
        TelSetTel telSetTel1 = null;
        if (telSetTelList != null && telSetTelList.size() > 0)
            telSetTel1 = telSetTelList.get(0);

        int success = deleteByCondition(condition);
        if (success == 1) {
            setRefreshCacheMethod("deleteCache", telSetTel1);
            return new ApiResult<>(ApiResult.SUCCESS_RESULT, "删除成功");
        }
        logger.error("TelSetTelServiceImp.deleteTelSetTel error " + telSetTel + "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT, "删除失败");
    }

    @Override
    public ApiResult updateTelSetTel(TelSetTel telSetTel) {
        if (!entityMapper.validateEntity(telSetTel.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT, "企业编号不正确");

        if (telSetTel.getSetId() == null || telSetTel.getId() <= 0)
            return new ApiResult(ApiResult.FAIL_RESULT, "id不能为空");

        ApiResult<TelSetTel> result = validateTelSetTel(telSetTel);
        if (result != null)
            return result;

        TelSetTel telSetTel1 = selectByPrimaryKey(telSetTel.getId());
        if (!telSetTel.getEnterpriseId().equals(telSetTel1.getEnterpriseId()))
            return new ApiResult(ApiResult.FAIL_RESULT, "企业编号和id不匹配");

        telSetTel.setCreateTime(telSetTel1.getCreateTime());

        int success = updateByPrimaryKey(telSetTel);
        if (success == 1) {
            setRefreshCacheMethod("setCache", telSetTel);
            return new ApiResult<>(ApiResult.SUCCESS_RESULT, "更新成功");
        }
        logger.error("TelSetTelServiceImp.updateTelSetTel error " + telSetTel + "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
    }

    @Override
    public ApiResult<List<TelSetTel>> listTelSetTel(TelSetTel telSetTel) {
        if (!entityMapper.validateEntity(telSetTel.getEnterpriseId()))
            return new ApiResult<>(ApiResult.FAIL_RESULT, "企业编号不正确");
        if (telSetTel.getSetId() == null || telSetTel.getSetId() == 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "电话组id不正确");
        Condition setCondition = new Condition(TelSet.class);
        Condition.Criteria setCriteria = setCondition.createCriteria();
        setCriteria.andEqualTo("id", telSetTel.getSetId());
        setCondition.setTableName("cti_link_tel_set");
        List<TelSet> setList = telSetMapper.selectByCondition(setCondition);
        if (setList == null || setList.size() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "不存在此电话组id");

        Condition condition = new Condition(TelSetTel.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", telSetTel.getEnterpriseId());
        criteria.andEqualTo("setId", telSetTel.getSetId());

        List<TelSetTel> telSetTelsList = selectByCondition(condition);
        if (telSetTelsList != null && telSetTelsList.size() > 0)
            return new ApiResult<>(telSetTelsList);
        return new ApiResult<>(ApiResult.FAIL_RESULT, "获取电话列表失败");
    }

    protected String getKey(TelSetTel telSetTel) {
        return String.format(CacheKey.TEL_SET_TEL_ENTERPRISE_TSNO,
                telSetTel.getEnterpriseId(), telSetTel.getTsno());
    }

    public void deleteCache(TelSetTel telSetTel) {
        List<TelSetTel> list = redisService.getList(Const.REDIS_DB_CONF_INDEX, getKey(telSetTel), TelSetTel.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(telSetTel.getId()))
                list.remove(i);
            break;
        }
        redisService.set(Const.REDIS_DB_CONF_INDEX, getKey(telSetTel), list);
    }

    public void setCache(TelSetTel telSetTel) {
        List<TelSetTel> list = redisService.getList(Const.REDIS_DB_CONF_INDEX, getKey(telSetTel), TelSetTel.class);
        if (list == null)
            list = new ArrayList<TelSetTel>();
        list.add(telSetTel);
        redisService.set(Const.REDIS_DB_CONF_INDEX, getKey(telSetTel), list);
    }

    private void setRefreshCacheMethod(String methodName, TelSetTel telSetTel) {
        try {
            Method method = this.getClass().getMethod(methodName, TelSetTel.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, telSetTel);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("TelSetTelServiceImp.setRefreshCacheMethod error,cache refresh fail," + "class=" +
                    this.getClass().getName(), e);
        }
    }

    private <T> ApiResult<T> validateTelSetTel(TelSetTel telSetTel) {
        if (telSetTel.getSetId() == null || telSetTel.getSetId() == 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "电话组id不正确");
        Condition setCondition = new Condition(TelSetTel.class);
        Condition.Criteria setCriteria = setCondition.createCriteria();
        setCriteria.andEqualTo("id", telSetTel.getSetId());
        List<TelSet> setList = telSetMapper.selectByCondition(setCondition);
        if (setList == null || setList.size() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "不存在此setId");
        telSetTel.setTsno(setList.get(0).getTsno());

        if (StringUtils.isEmpty(telSetTel.getTel()))
            return new ApiResult<>(ApiResult.FAIL_RESULT, "电话号码不能为空");
        Pattern pattern = Pattern.compile(Const.TEL_VALIDATION);
        Matcher matcher = pattern.matcher(telSetTel.getTel());
        if (!matcher.matches())
            return new ApiResult<>(ApiResult.FAIL_RESULT, "电话号码不正确");

        if (telSetTel.getTimeout() == null || telSetTel.getTimeout() < 5)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "超时时间为5-60秒");
        if (telSetTel.getTimeout() >= setList.get(0).getTimeout())
            return new ApiResult<>(ApiResult.FAIL_RESULT, "超时时间要小于所在电话组");

        if (telSetTel.getPriority() == null || telSetTel.getPriority() == 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT, "优先级不正确");

        return null;
    }
}
