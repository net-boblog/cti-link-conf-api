package com.tinet.ctilink;

import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.dao.*;
import com.tinet.ctilink.conf.model.EnterpriseInvestigation;
import com.tinet.ctilink.conf.model.EnterpriseIvr;
import com.tinet.ctilink.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

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

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {

		// 设置JVM的DNS缓存时间
		// http://docs.amazonaws.cn/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-jvm-ttl.html
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");

		//第一次启动, 加载所有缓存
		ContextUtil.getContext().getBean(GatewayDao.class).loadCache();

		ContextUtil.getContext().getBean(PublicMohDao.class).loadCache();

		ContextUtil.getContext().getBean(EnterpriseMohDao.class).loadCache();

		ContextUtil.getContext().getBean(RestrictTelDao.class).loadCache();

		ContextUtil.getContext().getBean(EnterpriseSettingDao.class).loadCache();

		ContextUtil.getContext().getBean(EnterpriseClidDao.class).loadCache();

		ContextUtil.getContext().getBean(EnterpriseHangupSetDao.class).loadCache();

		ContextUtil.getContext().getBean(EnterpriseInvestigationDao.class).loadCache();

		ContextUtil.getContext().getBean(EnterpriseIvrDao.class).loadCache();

		ContextUtil.getContext().getBean(TrunkDao.class).loadCache();

		ContextUtil.getContext().getBean(TelSetDao.class).loadCache();

		ContextUtil.getContext().getBean(TelSetTelDao.class).loadCache();

		ContextUtil.getContext().getBean(RouterDao.class).loadCache();

		ContextUtil.getContext().getBean(EnterpriseRouterDao.class).loadCache();


		logger.info("cti-link-conf启动成功");
		System.out.println("cti-clink-conf启动成功");
	}
}