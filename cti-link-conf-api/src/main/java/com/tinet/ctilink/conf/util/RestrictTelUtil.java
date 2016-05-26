package com.tinet.ctilink.conf.util;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.EnterpriseSetting;
import com.tinet.ctilink.conf.model.RestrictTel;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.inc.EnterpriseSettingConst;
import com.tinet.ctilink.util.ContextUtil;

/**
 * @author fengwei //
 * @date 16/5/26 22:04
 */
public class RestrictTelUtil {

    public static boolean isRestrictTel(int enterpriseId, String tel, int type) {
        RedisService redisService = ContextUtil.getBean(RedisService.class);
        EnterpriseSetting enterpriseSetting = redisService.get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_SETTING_ENTERPRISE_ID_NAME,
                enterpriseId, EnterpriseSettingConst.ENTERPRISE_SETTING_NAME_RESTRICT_TEL_TYPE), EnterpriseSetting.class);
        boolean isRestrictTel = false;
        if (null != enterpriseSetting) {
            String value = enterpriseSetting.getValue();
            if (!"".equals(value) && null != value) {
                Integer restrictType;
                if (value.equals("1")) {  //黑名单
                    restrictType = 1;
                    RestrictTel restrictTel = redisService.get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.RESTRICT_TEL_ENTERPRISE_ID_TYPE_RESTRICT_TYPE_TEL,
                            enterpriseId, type, restrictType, tel), RestrictTel.class);
                    if (restrictTel != null) {
                        isRestrictTel = true;
                    }
                } else if (value.equals("2")) {  //白名单
                    isRestrictTel = true;
                    restrictType = 2;
                    RestrictTel restrictTel = redisService.get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.RESTRICT_TEL_ENTERPRISE_ID_TYPE_RESTRICT_TYPE_TEL,
                            enterpriseId, type, restrictType, tel), RestrictTel.class);
                    if (restrictTel != null) {
                        isRestrictTel = false;
                    }
                }
            }
        }
        return isRestrictTel;
    }

}