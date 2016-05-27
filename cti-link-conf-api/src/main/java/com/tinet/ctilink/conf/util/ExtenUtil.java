package com.tinet.ctilink.conf.util;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.entity.Caller;
import com.tinet.ctilink.conf.model.AreaCode;
import com.tinet.ctilink.conf.model.CtiLinkExten;
import com.tinet.ctilink.conf.model.Gateway;
import com.tinet.ctilink.conf.model.SystemSetting;
import com.tinet.ctilink.inc.SystemSettingConst;
import com.tinet.ctilink.util.ContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author fengwei //
 * @date 16/4/22 10:02
 */
@Component
public class ExtenUtil {

    public static CtiLinkExten getExten(Integer enterpriseId, String exten) {
        RedisService redisService = ContextUtil.getBean(RedisService.class);
        
        CtiLinkExten ctiLinkExten = redisService.get(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.EXTEN_ENTERPRISE_ID_EXTEN
                            , enterpriseId, exten), CtiLinkExten.class);
           
        return ctiLinkExten;
    }

    
}
