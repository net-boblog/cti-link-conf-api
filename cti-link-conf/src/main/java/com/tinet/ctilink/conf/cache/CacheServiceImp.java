package com.tinet.ctilink.conf.cache;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.service.v1.CtiLinkCacheService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/17 13:35
 */
@Service
public class CacheServiceImp implements CtiLinkCacheService {
    @Autowired
    List<ConfCacheInterface> confCacheInterfaceList;

    @Override
    public String reloadCache() {
        Date start = new Date();
        if (confCacheInterfaceList != null) {
            for (ConfCacheInterface cacheInterface : confCacheInterfaceList) {
                cacheInterface.reloadCache();
            }
        }
        Date end = new Date();

        return "cache reloaded, total time :" + (end.getTime()/1000 - start.getTime()/1000);
    }
}
