package com.tinet.ctilink.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 获取spring的ApplicationContext。通过ApplicationContext可以获取在spring配置文件中配置的类。
 * <p>
 * 文件名： ContextUtil.java
 * <p>
 * Copyright (c) 2006-2010 T&I Net Communication CO.,LTD. All rights reserved.
 *
 * @author 周营昭
 * @since 1.0
 * @version 1.0
 */
public class ContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	/**
	 * 获取spring的ApplicationContext。
	 *
	 * @return Spring的ApplicationContext.
	 */
	public static ApplicationContext getContext() {
		return applicationContext;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ContextUtil.applicationContext = applicationContext;
	}

	/**
	 * 获取类型为requiredType的对象
	 *
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

}
