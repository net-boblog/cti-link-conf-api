package com.tinet.ctilink.core.dialect;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的数据库方言管理器实现类，对数据库方言对象进行管理。
 *
 */
public class DefaultDialectManager implements IDialectManager {

	private Map<String, IDialect> dialects;
	
	public DefaultDialectManager(){
		initDialects();
	}
	public void setDialects(Map<String, IDialect> dialects) {
		this.dialects = dialects;
		initDialects();
	}

	private IDialect defaultDialect = new Dialect();

	protected void initDialects() {
		if (dialects == null) {
			dialects = new HashMap<String, IDialect>();
			dialects.put("postgresql", new PostgresqlDialect());
		}
	}

	public IDialect getDialect(String dialectName) {
		IDialect dialect = dialects.get(dialectName);
		if (dialect == null) {
			dialect = defaultDialect;
		}
		return dialect;
	}

}
