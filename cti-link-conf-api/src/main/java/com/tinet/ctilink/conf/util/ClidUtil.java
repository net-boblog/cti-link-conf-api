package com.tinet.ctilink.conf.util;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.entity.Caller;
import com.tinet.ctilink.conf.model.EnterpriseClid;
import com.tinet.ctilink.conf.model.EnterpriseHotline;
import com.tinet.ctilink.conf.model.Trunk;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.util.ContextUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Random;

/**
 * @author fengwei //
 * @date 16/4/22 10:56
 */
public class ClidUtil {

    public static String getClid(int enterpriseId, int routerClidCallType, String customerNumber, String numberTrunk) {
        RedisService redisService = ContextUtil.getBean(RedisService.class);
        EnterpriseClid enterpriseClid = redisService.get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_CLID_ENTERPRISE_ID
                , enterpriseId), EnterpriseClid.class);

        int clidType = 0;
        String clidNumber = "";
        if (enterpriseClid != null) {
            switch (routerClidCallType) {
                case Const.ROUTER_CLID_CALL_TYPE_IB_RIGHT:// 1呼入
                    clidType = enterpriseClid.getIbClidRightType();
                    clidNumber = enterpriseClid.getIbClidRightNumber();
                    break;
                case Const.ROUTER_CLID_CALL_TYPE_PREVIEW_OB_LEFT:// 2预览外呼客户侧*
                    clidType = enterpriseClid.getObPreviewClidLeftType();
                    clidNumber = enterpriseClid.getObPreviewClidLeftNumber();
                    break;
                case Const.ROUTER_CLID_CALL_TYPE_PREVIEW_OB_RIGHT:// 3预览外呼座席侧
                    clidType = enterpriseClid.getObPreviewClidRightType();
                    clidNumber = enterpriseClid.getObPreviewClidRightNumber();
                    break;
                case Const.ROUTER_CLID_CALL_TYPE_PREDICTIVE_OB_LEFT://// 4预测外呼客户侧
                    clidType = enterpriseClid.getObPredictiveClidLeftType();
                    clidNumber = enterpriseClid.getObPredictiveClidLeftNumber();
                    break;
                case Const.ROUTER_CLID_CALL_TYPE_PREDICTIVE_OB_RIGHT:// 5预测外呼座席侧
                    clidType = enterpriseClid.getObPredictiveClidRightType();
                    clidNumber = enterpriseClid.getObPredictiveClidRightNumber();
                    break;
            }
            if (clidType != 0) {
                String clid = "";
                if (clidType == 1) {// 外显中继号码，选取主热线号码对应的中继号码
                	if(StringUtils.isEmpty(clidNumber)){
	                    Trunk trunk = redisService.get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.TRUNK_ENTERPRISE_ID_FIRST, enterpriseId), Trunk.class);
	                    if(trunk != null){
	                    	clid = trunk.getAreaCode() + trunk.getNumberTrunk();
	                    }
                	}else{
                		clid = clidNumber;
                	}

                } else if (clidType == 2) {// 外显客户号码
                    if (customerNumber.equals(Const.UNKNOWN_NUMBER)) {
                        List<EnterpriseHotline> enterpriseHotlineList = redisService.getList(Const.REDIS_DB_CONF_INDEX
                                , String.format(CacheKey.ENTERPRISE_HOTLINE_ENTERPRISE_ID, enterpriseId), EnterpriseHotline.class);
                        if (enterpriseHotlineList != null && enterpriseHotlineList.size() > 0) {
                            clid = enterpriseHotlineList.get(0).getNumberTrunk();
                        }
                    } else {
                        clid = customerNumber;
                    }
                } else if (clidType == 3) {// 外显固定号码
                    if (StringUtils.isNotEmpty(clidNumber)) {
                        String[] clidList = clidNumber.split(",");
                        if (clidList.length > 1) {
                            Random r = new Random(); // 实例化一个Random类
                            // 随机产生一个整数
                            clid = clidList[r.nextInt(clidList.length)];
                        } else {
                            clid = clidNumber;
                        }
                    }
                } else if (clidType == 4) { // 外显热线号码
                    EnterpriseHotline enterpriseHotline;
                    if (StringUtils.isNotEmpty(numberTrunk) && StringUtils.isNumeric(numberTrunk)) {
                        enterpriseHotline = redisService.get(Const.REDIS_DB_CONF_INDEX
                                , String.format(CacheKey.ENTERPRISE_HOTLINE_ENTERPRISE_ID_NUMBER_TRUNK, enterpriseId, numberTrunk)
                                , EnterpriseHotline.class);
                    } else {
                        List<EnterpriseHotline> enterpriseHotlineList = redisService.getList(Const.REDIS_DB_CONF_INDEX
                                , String.format(CacheKey.ENTERPRISE_HOTLINE_ENTERPRISE_ID, enterpriseId), EnterpriseHotline.class);
                        enterpriseHotline = enterpriseHotlineList.get(0);
                    }
                    if (enterpriseHotline != null) {
                        clid = enterpriseHotline.getHotline();
                    }
                }
                return clid;
            }
        }
        return null;
    }
    
    public static boolean isClidValid(int enterpriseId, int routerClidCallType, String customerNumber, String clid) {
        RedisService redisService = ContextUtil.getBean(RedisService.class);
        EnterpriseClid enterpriseClid = redisService.get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_CLID_ENTERPRISE_ID
                , enterpriseId), EnterpriseClid.class);

        int clidType = 0;
        String clidNumber = "";
        if (enterpriseClid != null) {
            switch (routerClidCallType) {
                case Const.ROUTER_CLID_CALL_TYPE_IB_RIGHT:// 1呼入
                    clidType = enterpriseClid.getIbClidRightType();
                    clidNumber = enterpriseClid.getIbClidRightNumber();
                    break;
                case Const.ROUTER_CLID_CALL_TYPE_PREVIEW_OB_LEFT:// 2预览外呼客户侧*
                    clidType = enterpriseClid.getObPreviewClidLeftType();
                    clidNumber = enterpriseClid.getObPreviewClidLeftNumber();
                    break;
                case Const.ROUTER_CLID_CALL_TYPE_PREVIEW_OB_RIGHT:// 3预览外呼座席侧
                    clidType = enterpriseClid.getObPreviewClidRightType();
                    clidNumber = enterpriseClid.getObPreviewClidRightNumber();
                    break;
                case Const.ROUTER_CLID_CALL_TYPE_PREDICTIVE_OB_LEFT://// 4预测外呼客户侧
                    clidType = enterpriseClid.getObPredictiveClidLeftType();
                    clidNumber = enterpriseClid.getObPredictiveClidLeftNumber();
                    break;
                case Const.ROUTER_CLID_CALL_TYPE_PREDICTIVE_OB_RIGHT:// 5预测外呼座席侧
                    clidType = enterpriseClid.getObPredictiveClidRightType();
                    clidNumber = enterpriseClid.getObPredictiveClidRightNumber();
                    break;
            }
            if (clidType == 1) {// 外显中继号码，选取主热线号码对应的中继号码
            	Caller caller = AreaCodeUtil.updateGetAreaCode(clid, "");
            	if(caller != null){
	            	Trunk trunk = redisService.get(Const.REDIS_DB_CONF_INDEX
	                        , String.format(CacheKey.TRUNK_NUMBER_TRUNK, caller.getRealNumber()), Trunk.class);
	                if (trunk != null && trunk.getAreaCode().equals(caller.getAreaCode())) {
	                    return true;
	                }
            	}

            } else if (clidType == 2) {// 外显客户号码
                if (customerNumber.equals(clid)){
                	return true;
                }
            } else if (clidType == 3) {// 外显固定号码
                if (StringUtils.isNotEmpty(clidNumber)) {
                    String[] clidList = clidNumber.split(",");
                    for(String c: clidList){
                    	if(c.equals(clid)){
                    		return true;
                    	}
                    }
                }
            } else if (clidType == 4) { // 外显热线号码
                List<EnterpriseHotline> enterpriseHotlineList = redisService.getList(Const.REDIS_DB_CONF_INDEX
                        , String.format(CacheKey.ENTERPRISE_HOTLINE_ENTERPRISE_ID, enterpriseId), EnterpriseHotline.class);
                for(EnterpriseHotline hotline :enterpriseHotlineList){
                	if(hotline.getHotline().equals(clid)){
                		return true;
                	}
                }
            }
        }
        return false;
    }
}
