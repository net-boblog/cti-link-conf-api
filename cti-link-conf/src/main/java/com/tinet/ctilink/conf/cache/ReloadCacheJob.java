package com.tinet.ctilink.conf.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/10 10:21
 */
public class ReloadCacheJob implements Runnable {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private List<ConfCacheInterface> confCacheInterfaceList;

    @Override
    public void run() {
        Date start = new Date();
        logger.info("reload start , startTime:" + start.getTime()/1000);
        if (confCacheInterfaceList != null) {
            for (ConfCacheInterface cacheInterface : confCacheInterfaceList) {
                cacheInterface.reloadCache();
            }
        }
        Date end = new Date();
        logger.info("reload end , endTime:" + end.getTime()/1000 + ", 耗时" + (end.getTime()/1000 - start.getTime()/1000) + "秒");
    }

    public List<ConfCacheInterface> getConfCacheInterfaceList() {
        return confCacheInterfaceList;
    }

    public void setConfCacheInterfaceList(List<ConfCacheInterface> confCacheInterfaceList) {
        this.confCacheInterfaceList = confCacheInterfaceList;
    }
}
