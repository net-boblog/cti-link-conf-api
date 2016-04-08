package com.tinet.ctilink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author fengwei //
 * @date 16/4/8 16:19
 */
public class ApplicationStarter implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LoggerFactory.getLogger(ApplicationStarter.class);

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {

        // 设置JVM的DNS缓存时间
        // http://docs.amazonaws.cn/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-jvm-ttl.html
        java.security.Security.setProperty("networkaddress.cache.ttl", "60");


        logger.info("cti-link-conf启动成功");
        System.out.println("cti-link-conf启动成功");
    }
}
