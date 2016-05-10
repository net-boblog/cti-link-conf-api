package com.tinet.ctilink;

import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.cache.ConfCacheInterface;
import com.tinet.ctilink.conf.cache.ReloadCacheJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用程序启动器
 * 
 * @author Jiangsl
 *
 */
@Component
public class ApplicationStarter implements ApplicationListener<ContextRefreshedEvent> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	RedisService redisService;

	@Autowired
	List<ConfCacheInterface> confCacheInterfaceList;

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {

		// 设置JVM的DNS缓存时间
		// http://docs.amazonaws.cn/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-jvm-ttl.html
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");

		//配置了加载所有缓存  -DloadCache=true
		String loadCache = System.getProperty("loadCache");
		//if (loadCache != null && "true".equals(loadCache)) {
		ReloadCacheJob reloadCacheJob = new ReloadCacheJob();
		reloadCacheJob.setConfCacheInterfaceList(confCacheInterfaceList);
		Thread reloadThread = new Thread(reloadCacheJob);
		reloadThread.start();
		//}

		logger.info("cti-link-conf启动成功");
		System.out.println("cti-clink-conf启动成功");
	}
}