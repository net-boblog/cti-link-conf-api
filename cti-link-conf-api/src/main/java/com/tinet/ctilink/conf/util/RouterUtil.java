package com.tinet.ctilink.conf.util;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.EnterpriseRouter;
import com.tinet.ctilink.conf.model.Gateway;
import com.tinet.ctilink.conf.model.Router;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.entity.Caller;
import com.tinet.ctilink.util.ContextUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/22 10:18
 */
@Component
public class RouterUtil {

    public static Router getRouter (int enterpriseId, int routerClidCallType, Caller caller) {
    	Integer routersetId = getRoutersetId(enterpriseId, routerClidCallType);
 
    	Router router = getRouter(routersetId, caller);

        return router;
    }
    public static Integer getRoutersetId(int enterpriseId, int routerClidCallType){
        EnterpriseRouter enterpriseRouter = ContextUtil.getBean(RedisService.class).get(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.ENTERPRISE_ROUTER_ENTERPRISE_ID, enterpriseId), EnterpriseRouter.class);
        
        if(enterpriseRouter == null){
            return null;
        }
        int routersetId;
        switch(routerClidCallType){
            case Const.ROUTER_CLID_CALL_TYPE_IB_RIGHT:
                routersetId = enterpriseRouter.getIbRouterRight();
                break;
            case Const.ROUTER_CLID_CALL_TYPE_PREVIEW_OB_LEFT:
                routersetId = enterpriseRouter.getObPreviewRouterLeft();
                break;
            case Const.ROUTER_CLID_CALL_TYPE_PREVIEW_OB_RIGHT:
                routersetId = enterpriseRouter.getIbRouterRight();
                break;
            case Const.ROUTER_CLID_CALL_TYPE_PREDICTIVE_OB_LEFT:
                routersetId = enterpriseRouter.getObPredictiveRouterLeft();
                break;
            case Const.ROUTER_CLID_CALL_TYPE_PREDICTIVE_OB_RIGHT:
                routersetId = enterpriseRouter.getIbRouterRight();
                break;
            default:
                routersetId = enterpriseRouter.getObPreviewRouterLeft();
        }
        return routersetId;
    }
    
    public static Router getRouterInternal(int routersetId, String exten){
    	Router router = null;
    	List<Router> routerList = ContextUtil.getBean(RedisService.class)
                 .getList(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ROUTER_ROUTERSET_ID, routersetId), Router.class);
         //找prefix
         for (Router r : routerList) {
        	 if(r.getType().equals(Const.ROUTER_TYPE_INTERNAL)){
        		 if (exten.startsWith(r.getPrefix())) {
                     if (router == null) {
                         router = r;
                     } else {
                         //最长匹配
                         if (r.getPrefix().length() > router.getPrefix().length()) {
                             router = r;
                         } else if (r.getPrefix().equals(router.getPrefix())
                                 && r.getPriority() > router.getPriority()) {
                             router = r;
                         }
                     }
                 }
        	 }
         }
         return null;
    }
    
    public static Router getRouter(int routersetId, Caller caller) {
        Router router = null;

        String routerTel = caller.getCallerNumber();
        if (caller.getTelType() == Const.TEL_TYPE_MOBILE) {
            routerTel = caller.getAreaCode() + caller.getCallerNumber();
        }
        List<Router> routerList = ContextUtil.getBean(RedisService.class)
                .getList(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ROUTER_ROUTERSET_ID, routersetId), Router.class);
        //找prefix
        for (Router r : routerList) {
        	if(r.getType().equals(Const.ROUTER_TYPE_INTERNAL)){
	            if (routerTel.startsWith(r.getPrefix())) {
	                if (router == null) {
	                    router = r;
	                } else {
	                    //最长匹配
	                    if (r.getPrefix().length() > router.getPrefix().length()) {
	                        router = r;
	                    } else if (r.getPrefix().equals(router.getPrefix())
	                            && r.getPriority() > router.getPriority()) {
	                        router = r;
	                    }
	                }
	            }
        	}
        }

        return router;
    }

    public static Gateway getRouterGateway(int enterpriseId, int routerClidCallType, Caller caller) {
        Router router = getRouter(enterpriseId, routerClidCallType, caller);

        if (router != null) {
            Gateway gateway = ContextUtil.getBean(RedisService.class)
                    .get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.GATEWAY_ID, router.getGatewayId()), Gateway.class);
            if(gateway != null){	
	            if (caller.getTelType() == Const.TEL_TYPE_MOBILE) {
	            	gateway.setPrefix(gateway.getPrefix() + caller.getAreaCode());
	                return gateway;
	            }
    		}
        }
        return null;
    }
    public static Gateway getRouterGatewayInternal(int enterpriseId, int routerClidCallType, String exten) {
        Integer routersetId = getRoutersetId(enterpriseId, routerClidCallType);
    	Router router = getRouterInternal(routersetId, exten);

        if (router != null) {
        	Gateway gateway = ContextUtil.getBean(RedisService.class)
                    .get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.GATEWAY_ID, router.getGatewayId()), Gateway.class);
            return gateway;	
        }
        return null;
    }
}
