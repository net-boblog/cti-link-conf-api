package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.dao.EnterpriseTimeDao;
import com.tinet.ctilink.conf.dao.IvrProfileDao;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.model.EnterpriseIvrRouter;
import com.tinet.ctilink.conf.model.EnterpriseTime;
import com.tinet.ctilink.conf.model.IvrProfile;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseIvrRouterService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nope-J on 2016/5/4.
 */
@Service
public class EnterpriseIvrRouterServiceImp extends BaseService<EnterpriseIvrRouter> implements CtiLinkEnterpriseIvrRouterService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisService redisService;

    @Autowired
    private IvrProfileDao ivrProfileDao;

    @Autowired
    private EnterpriseTimeDao enterpriseTimeDao;

    @Override
    public ApiResult<EnterpriseIvrRouter> createEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter) {
        if(enterpriseIvrRouter.getEnterpriseId() == null || enterpriseIvrRouter.getEnterpriseId() <=0 )
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");

        if(enterpriseIvrRouter.getActive() == null || !(enterpriseIvrRouter.getActive()==1 || enterpriseIvrRouter.getActive()==2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"是否启用：1.启用 2.停用");

        if(enterpriseIvrRouter.getRouterType() == null || !(enterpriseIvrRouter.getRouterType()==1
        || enterpriseIvrRouter.getRouterType()==2 || enterpriseIvrRouter.getRouterType()==3))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"路由目的类型：1.IVR 2.固定电话 3.分机号");

        if(enterpriseIvrRouter.getRouterProperty().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"路由目的值不能为空");

        //语音导航id
        if(enterpriseIvrRouter.getRouterType() == 1){
            Condition ivrProfileCondition = new Condition(IvrProfile.class);
            Condition.Criteria ivrProfileCriteria  = ivrProfileCondition.createCriteria();
            ivrProfileCriteria.andEqualTo("id",Integer.parseInt(enterpriseIvrRouter.getRouterProperty()));
            ivrProfileCondition.setTableName("cti_link_ivr_profile");
            List<IvrProfile> ivrProfileList = ivrProfileDao.selectByCondition(ivrProfileCondition);
            if(ivrProfileList == null || ivrProfileList.size() <= 0)
                return new ApiResult<>(ApiResult.FAIL_RESULT,"语音导航id不正确");
        }

        //固定电话号码
        if(enterpriseIvrRouter.getRouterType() == 2){
            Pattern pattern = Pattern.compile(Const.TEL_VALIDATION);
            Matcher matcher = pattern.matcher(enterpriseIvrRouter.getRouterProperty());
            if ( ! matcher.matches())
                return new ApiResult<>(ApiResult.FAIL_RESULT,"固定号码不正确");
        }

        //分机号码
        if(enterpriseIvrRouter.getRouterType() ==3 ){
            Pattern pattern = Pattern.compile(Const.EXTEN_TEL_VALIDATION);
            Matcher matcher = pattern.matcher(enterpriseIvrRouter.getRouterProperty());
            if( ! matcher.matches())
                return new ApiResult<>(ApiResult.FAIL_RESULT,"分机号码不正确");
        }

        if(enterpriseIvrRouter.getPriority() == null || enterpriseIvrRouter.getPriority() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"优先级不能小于或等于0");

        //时间条件id(可选项)
        if( ! enterpriseIvrRouter.getRuleTimeProperty().isEmpty()){
            String[] timeId = enterpriseIvrRouter.getRuleTimeProperty().split(";");
            for(int i=0;i<timeId.length;i++)
            System.out.println("时间条件id为："+ timeId[i]);

            for(int i=0; i<timeId.length; i++ ){
                Condition timeCondition = new Condition(EnterpriseTime.class);
                Condition.Criteria timeCriteria = timeCondition.createCriteria();
                timeCriteria.andEqualTo("id",Integer.parseInt(timeId[i]));
                timeCriteria.andEqualTo("enterpriseId",enterpriseIvrRouter.getEnterpriseId());
                timeCondition.setTableName("cti_link_enterprise_time");
                List<EnterpriseTime> enterpriseTimeList = enterpriseTimeDao.selectByCondition(timeCondition);
                if(enterpriseTimeList == null || enterpriseTimeList.size() <= 0)
                    return new ApiResult<>(ApiResult.FAIL_RESULT,"时间条件id不正确");
            }
        }

        //来电地区规则(可选项)
        if( ! enterpriseIvrRouter.getRuleAreaProperty().isEmpty()){
            String[] ruleArea = enterpriseIvrRouter.getRuleAreaProperty().split(";");
            Pattern pattern = Pattern.compile(Const.AREA_CODE_VALIDATION+"|"+Const.TEL_VALIDATION);

            for(int i=0; i<ruleArea.length; i++){
                Matcher matcher = pattern.matcher(ruleArea[i]);
                if( ! matcher.matches())
                    return new ApiResult<>(ApiResult.FAIL_RESULT,"来电地区不符合规则");
            }
        }

        //中继号码规则(可选项)
        if( ! enterpriseIvrRouter.getRuleTrunkProperty().isEmpty()){
            String[] ruleTrunk = enterpriseIvrRouter.getRuleTrunkProperty().split(";");
            Pattern pattern = Pattern.compile(Const.TEL_VALIDATION);

            for (int i=0;i<ruleTrunk.length;i++){
                Matcher matcher = pattern.matcher(ruleTrunk[i]);
                if( ! matcher.matches())
                    return new ApiResult<>(ApiResult.FAIL_RESULT,"中继号码不符合规则");
            }
        }

        enterpriseIvrRouter.setCreateTime(new Date());

        int success = insertSelective(enterpriseIvrRouter);

        if(success == 1){
            setRefreshCacheMethod("setCache",enterpriseIvrRouter);
            return new ApiResult<>(enterpriseIvrRouter);
        }
        logger.error("EnterpriseIvrRouterServiceImp.createEnterpriseIvrRouter error " + enterpriseIvrRouter + "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"添加失败");
    }

    @Override
    public ApiResult deleteEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter) {
        if(enterpriseIvrRouter.getEnterpriseId() == null || enterpriseIvrRouter.getEnterpriseId() <= 0)
            return new ApiResult(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseIvrRouter.getId() == null || enterpriseIvrRouter.getId() <= 0)
            return new ApiResult(ApiResult.FAIL_RESULT,"呼入路由id不正确");

        Condition condition = new Condition(EnterpriseIvrRouter.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseIvrRouter.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseIvrRouter.getId());
        int success = deleteByCondition(condition);

        if(success == 1){
            setRefreshCacheMethod("deleteCache",enterpriseIvrRouter);
            return new ApiResult(enterpriseIvrRouter);
        }
        logger.error("EnterprisIvrRouterServiceImp.deleteEnterpriseIvrRouter error refresh cache fail "+ enterpriseIvrRouter
                + "success=" + success);
        return new ApiResult(ApiResult.FAIL_RESULT,"删除失败");
    }

    @Override
    public ApiResult<EnterpriseIvrRouter> updateEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter) {
        if (enterpriseIvrRouter.getId() == null || enterpriseIvrRouter.getId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"呼入路由id不正确");

        if(enterpriseIvrRouter.getEnterpriseId() == null || enterpriseIvrRouter.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业id不正确");

        EnterpriseIvrRouter enterpriseIvrRouter1 = selectByPrimaryKey(enterpriseIvrRouter);

        if( ! enterpriseIvrRouter.getEnterpriseId().equals(enterpriseIvrRouter1.getEnterpriseId()))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"路由id和企业id不匹配");

        if(enterpriseIvrRouter.getActive() == null || !(enterpriseIvrRouter.getActive()==1 || enterpriseIvrRouter.getActive()==2))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"是否启用：1.启用 2.停用");

        if(enterpriseIvrRouter.getRouterType() == null || !(enterpriseIvrRouter.getRouterType()==1
                || enterpriseIvrRouter.getRouterType()==2 || enterpriseIvrRouter.getRouterType()==3))
            return new ApiResult<>(ApiResult.FAIL_RESULT,"路由目的类型：1.IVR 2.固定电话 3.分机号");

        if(enterpriseIvrRouter.getRouterProperty().isEmpty())
            return new ApiResult<>(ApiResult.FAIL_RESULT,"路由目的值不能为空");

        //语音导航id
        if(enterpriseIvrRouter.getRouterType() == 1) {
            Condition ivrProfileCondition = new Condition(IvrProfile.class);
            Condition.Criteria ivrProfileCriteria  = ivrProfileCondition.createCriteria();
            ivrProfileCriteria.andEqualTo("id",Integer.parseInt(enterpriseIvrRouter.getRouterProperty()));
            ivrProfileCondition.setTableName("cti_link_ivr_profile");
            List<IvrProfile> ivrProfileList = ivrProfileDao.selectByCondition(ivrProfileCondition);
            if(ivrProfileList == null || ivrProfileList.size() <= 0)
                return new ApiResult<>(ApiResult.FAIL_RESULT,"语音导航id不正确");
        }

        //固定电话号码
        if(enterpriseIvrRouter.getRouterType() == 2){
            Pattern pattern = Pattern.compile(Const.TEL_VALIDATION);
            Matcher matcher = pattern.matcher(enterpriseIvrRouter.getRouterProperty());
            if ( ! matcher.matches())
                return new ApiResult<>(ApiResult.FAIL_RESULT,"固定号码不正确");
        }

        //分机号码
        if(enterpriseIvrRouter.getRouterType() ==3 ){
            Pattern pattern = Pattern.compile(Const.EXTEN_TEL_VALIDATION);
            Matcher matcher = pattern.matcher(enterpriseIvrRouter.getRouterProperty());
            if( ! matcher.matches())
                return new ApiResult<>(ApiResult.FAIL_RESULT,"分机号码不正确");
        }

        if(enterpriseIvrRouter.getPriority() == null || enterpriseIvrRouter.getPriority() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"优先级不能小于或等于0");

        //时间条件id(可选项)
        if( ! enterpriseIvrRouter.getRuleTimeProperty().isEmpty()){
            String[] timeId = enterpriseIvrRouter.getRuleTimeProperty().split(";");


            for(int i=0; i<timeId.length; i++ ){
                Condition timeCondition = new Condition(EnterpriseTime.class);
                Condition.Criteria timeCriteria = timeCondition.createCriteria();
                timeCriteria.andEqualTo("id",Integer.parseInt(timeId[i]));
                timeCondition.setTableName("cti_link_enterprise_time");
                List<EnterpriseTime> enterpriseTimeList = enterpriseTimeDao.selectByCondition(timeCondition);
                if(enterpriseTimeList == null || enterpriseTimeList.size() <= 0)
                    return new ApiResult<>(ApiResult.FAIL_RESULT,"时间条件id不正确");
            }
        }
        //来电地区规则(可选项)
        if( ! enterpriseIvrRouter.getRuleAreaProperty().isEmpty()){
            String[] ruleArea = enterpriseIvrRouter.getRuleAreaProperty().split(";");
            Pattern pattern = Pattern.compile(Const.AREA_CODE_VALIDATION+"|"+Const.TEL_VALIDATION);

            for(int i=0; i<ruleArea.length; i++){
                Matcher matcher = pattern.matcher(ruleArea[i]);
                if( ! matcher.matches())
                    return new ApiResult<>(ApiResult.FAIL_RESULT,"来电地区不符合规则");
            }
        }

        //中继号码规则(可选项)
        if( ! enterpriseIvrRouter.getRuleTrunkProperty().isEmpty()){
            String[] ruleTrunk = enterpriseIvrRouter.getRuleTrunkProperty().split(";");
            Pattern pattern = Pattern.compile(Const.TEL_VALIDATION);

            for (int i=0;i<ruleTrunk.length;i++){
                Matcher matcher = pattern.matcher(ruleTrunk[i]);
                if( ! matcher.matches())
                    return new ApiResult<>(ApiResult.FAIL_RESULT,"中继号码不符合规则");
            }
        }
        enterpriseIvrRouter.setCreateTime(enterpriseIvrRouter1.getCreateTime());

        int success = updateByPrimaryKey(enterpriseIvrRouter);

        if(success == 1){
            setRefreshCacheMethod("setCache",enterpriseIvrRouter);
            return new ApiResult<>(enterpriseIvrRouter);
        }
        logger.error("EnterpriseIvrRouterServiceImp.updateEnterpriseIvrRouter error " + enterpriseIvrRouter + "success=" + success);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"更新失败");
    }

    @Override
    public ApiResult<List<EnterpriseIvrRouter>> listEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter) {
        if(enterpriseIvrRouter.getEnterpriseId() == null || enterpriseIvrRouter.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");

        Condition condition = new Condition(EnterpriseIvrRouter.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseIvrRouter.getEnterpriseId());
        List<EnterpriseIvrRouter> enterpriseIvrRouterList = selectByCondition(condition);

        if(enterpriseIvrRouterList != null && enterpriseIvrRouterList.size() > 0)
            return new ApiResult<>(enterpriseIvrRouterList);
        return new ApiResult<>(ApiResult.FAIL_RESULT,"获取呼入路由列表失败");
    }

    @Override
    public ApiResult<EnterpriseIvrRouter> getEnterpriseIvrRouter(EnterpriseIvrRouter enterpriseIvrRouter) {
        if(enterpriseIvrRouter.getEnterpriseId() == null || enterpriseIvrRouter.getEnterpriseId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"企业编号不正确");
        if(enterpriseIvrRouter.getId() == null || enterpriseIvrRouter.getId() <= 0)
            return new ApiResult<>(ApiResult.FAIL_RESULT,"呼入路由id不正确");

        Condition condition = new Condition(EnterpriseIvrRouter.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId",enterpriseIvrRouter.getEnterpriseId());
        criteria.andEqualTo("id",enterpriseIvrRouter.getId());
        List<EnterpriseIvrRouter> enterpriseIvrRouterList = selectByCondition(condition);

        if(enterpriseIvrRouterList !=null && enterpriseIvrRouterList.size() > 0)
            return new ApiResult<>(enterpriseIvrRouterList.get(0));
        return new ApiResult<>(ApiResult.FAIL_RESULT,"获取呼入路由信息失败");
    }

    protected String getKey(EnterpriseIvrRouter enterpriseIvrRouter) {
        return String.format(CacheKey.ENTERPRISE_IVR_ROUTER_ENTERPRISE_ID,enterpriseIvrRouter.getEnterpriseId());
    }

    public void setCache(EnterpriseIvrRouter enterpriseIvrRouter){
        redisService.set(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseIvrRouter),enterpriseIvrRouter);
    }

    public void deleteCache(EnterpriseIvrRouter enterpriseIvrRouter){
        redisService.delete(Const.REDIS_DB_CONF_INDEX,getKey(enterpriseIvrRouter));
    }

    private void setRefreshCacheMethod(String methodName,EnterpriseIvrRouter enterpriseIvrRouter){
        try{
            Method method = this.getClass().getMethod(methodName,EnterpriseIvrRouter.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method,this,enterpriseIvrRouter);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        }catch(Exception e){
            logger.error("EnterpriseIvrRouterServiceImp.setRefreshCacheMethod error refresh Cache fail class = " + this.getClass().getName(),e);
        }
    }
}
