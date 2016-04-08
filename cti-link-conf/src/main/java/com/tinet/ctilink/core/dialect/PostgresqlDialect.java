package com.tinet.ctilink.core.dialect;
/**
 */
public class PostgresqlDialect extends Dialect{

	public boolean supportsLimitOffset(){
		return true;
	}
	
    public boolean supportsLimit() {   
        return true;   
    }  
    
    public String getLimitString(String sql, int offset, int limit) {
        if (offset > 0) {   
        	return sql + " limit "+limit+" offset "+offset; 
        } else {   
            return sql + " limit "+limit;
 
        }  
	}   
  
}
