package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.cache.ConfCacheInterface;
import com.tinet.ctilink.conf.cache.QueueMemberCacheService;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.entity.Caller;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.mapper.RoutersetMapper;
import com.tinet.ctilink.conf.model.*;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseRouterService;
import com.tinet.ctilink.conf.util.AreaCodeUtil;
import com.tinet.ctilink.conf.util.RouterUtil;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author fengwei //
 * @date 16/5/31 17:39
 */
@Service
public class EnterpriseRouterServiceImp extends BaseService<EnterpriseRouter> implements CtiLinkEnterpriseRouterService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private RoutersetMapper routersetMapper;

    @Autowired
    private QueueMemberServiceImp queueMemberService;

    @Autowired
    @Qualifier("queueMemberCacheService")
    private ConfCacheInterface queueMemberCacheService;

    @Autowired
    private RedisService redisService;

    @Override
    public ApiResult<EnterpriseRouter> updateEnterpriseRouter(EnterpriseRouter enterpriseRouter) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseRouter.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }

        if (enterpriseRouter.getIbRouterRight() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[ibRouterRight]不正确");
        }

        if (enterpriseRouter.getObPredictiveRouterLeft() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPredictiveRouterLeft]不正确");
        }

        if (enterpriseRouter.getObPreviewRouterLeft() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPreviewRouterLeft]不正确");
        }

        if (enterpriseRouter.getId() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]不正确");
        }

        EnterpriseRouter dbEnterpriseRouter = selectByPrimaryKey(enterpriseRouter.getId());
        if (dbEnterpriseRouter == null || !enterpriseRouter.getEnterpriseId().equals(dbEnterpriseRouter.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[id]或[enterpriseId]不正确");
        }

        //判断是否在routerset中
        Condition condition = new Condition(Routerset.class);
        Condition.Criteria criteria = condition.createCriteria();
        List<Integer> routersetIdList = new ArrayList<>();
        routersetIdList.add(enterpriseRouter.getIbRouterRight());
        routersetIdList.add(enterpriseRouter.getObPredictiveRouterLeft());
        routersetIdList.add(enterpriseRouter.getObPreviewRouterLeft());
        criteria.andIn("id", routersetIdList);
        List<Routerset> routersetList = routersetMapper.selectByCondition(condition);
        int flag = 0;
        for (Routerset routerset : routersetList) {
            if (routerset.getId().equals(enterpriseRouter.getIbRouterRight())) {
                flag = flag | 1;
            }
            if (routerset.getId().equals(enterpriseRouter.getObPredictiveRouterLeft())) {
                flag = flag | 2;
            }
            if (routerset.getId().equals(enterpriseRouter.getObPreviewRouterLeft())) {
                flag = flag | 4;
            }
        }
        if (flag != 7) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数不正确");
        }

        enterpriseRouter.setCreateTime(dbEnterpriseRouter.getCreateTime());
        int count = updateByPrimaryKeySelective(enterpriseRouter);

        if (count != 1) {
            logger.error("EnterpriseClidServiceImp.updateEnterpriseRouter error, " + enterpriseRouter + ", count=" + count);
            return new ApiResult<>(ApiResult.FAIL_RESULT, "更新失败");
        }

        //更改queue_member中的interface
        Condition condition1 = new Condition(QueueMember.class);
        Condition.Criteria criteria1 = condition1.createCriteria();
        criteria1.andEqualTo("enterpriseId", enterpriseRouter.getEnterpriseId());
        criteria1.andNotEqualTo("tel", "");
        List<QueueMember> queueMemberList = queueMemberService.selectByCondition(condition1);

        for (QueueMember queueMember : queueMemberList) {
            if (StringUtils.isEmpty(queueMember.getTel())) {
                continue;
            }
            // 判断只有固话和手机更新路由，软电话与分机不更新
            List<AgentTel> agentTelList = redisService.getList(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.AGENT_TEL_ENTERPRISE_ID
                    , enterpriseRouter.getEnterpriseId()), AgentTel.class);
            if (agentTelList != null) {
                AgentTel agentTel = agentTelList.get(0);
                if (agentTel.getIsBind() == Const.AGENT_TEL_IS_BIND_YES) {
                    if (agentTel.getTelType() == Const.TEL_TYPE_LANDLINE
                            || agentTel.getTelType() == Const.TEL_TYPE_MOBILE) {
                        Caller caller = AreaCodeUtil.updateGetAreaCode(queueMember.getTel(), "");
                        Gateway gateway = RouterUtil.getRouterGateway(enterpriseRouter.getEnterpriseId(), Const.ROUTER_CLID_CALL_TYPE_IB_RIGHT, caller);

                        if (gateway != null) {
                            String destInterface = "PJSIP/" + gateway.getName()+"/sip:"+gateway.getPrefix() + caller.getCallerNumber() + "@"
                                    + gateway.getIpAddr() + ":" + gateway.getPort();
                            queueMember.setInterface(destInterface);

                            queueMemberService.updateByPrimaryKey(queueMember);
                        }
                    }
                }
            }
        }
        setRefreshCacheMethod("setCache", enterpriseRouter);
        return new ApiResult<>(enterpriseRouter);
    }

    public void setCache(EnterpriseRouter enterpriseRouter) {
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_ROUTER_ENTERPRISE_ID
                , enterpriseRouter.getEnterpriseId()), enterpriseRouter);

        //重新加载queue member的缓存
        ((QueueMemberCacheService)queueMemberCacheService).reloadCache(enterpriseRouter.getEnterpriseId());
    }

    private void setRefreshCacheMethod(String methodName, EnterpriseRouter enterpriseRouter) {
        try {
            Method method = this.getClass().getMethod(methodName, EnterpriseRouter.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, enterpriseRouter);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("EnterpriseRouterServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }
}
