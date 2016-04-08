package com.tinet.ctilink.inc;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author fengwei //
 * @date 16/4/7 13:45
 */
public class Macro extends PropertyPlaceholderConfigurer {

	// 应用程序配置项
	private static Map<String, String> properties;

	/**
	 * 重写Spring的方法，将Properties加载至本地内存
	 */
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) {
		super.processProperties(beanFactory, props);

		properties = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			properties.put(keyStr, props.getProperty(keyStr));
		}
	}

	/**
	 * 读取应用程序配置项
	 *
	 * @param key properties文件中的配置项名称
	 * @return properties文件中的配置项值
	 */
	public static String getProperty(String key) {
		return properties.get(key);
	}

}
