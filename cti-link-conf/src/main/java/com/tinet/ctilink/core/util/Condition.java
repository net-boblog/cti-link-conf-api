package com.tinet.ctilink.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sql简单条件操作类
 * <p/>
 * 文件名： ConditionUtil.java
 * <p/>
 * Copyright (c) 2006-2010 T&I Net Communication CO.,LTD.  All rights reserved.
 *
 * @author 邹波
 * @version 1.0
 * @since 1.0
 */
public class Condition {
    private List<Map<String, Object>> conditions = new ArrayList<Map<String, Object>>();
    private Integer limit;//每页查询条数
    private Integer start;//查询起始条数
    private StringBuilder sortBuilder=new StringBuilder();//排序字符串，格式如： create_time desc , id asc
    private String statement;//指定需要执行的statement，对应mapper.xml里的ID<br>
    public static final String SORT_DESC = "desc";
    public static final String SORT_ASC = "asc";
    public static final String EQ = "=";
    public static final String IS = "is";
    public static final String IS_NOT = "is not";
    public static final String NE = "<>";
    public static final String IN = "in";
    public static final String NOT_IN = "not in";
    public static final String GT = ">";
    public static final String GT_EQ = ">=";
    public static final String LT = "<";
    public static final String LT_EQ = "<=";
    public static final String LIKE = "like";
    public static final String NOT_LIKE = "not like";
    static private Condition c;
    private Condition(){}
    static public Condition newInstance() {
        c = new Condition();
        return c;
    }

    public List<Map<String, Object>> getConditions() {
        return conditions;
    }

    /**
     * 设置条件，可以设置任意多个条件<br>使用方法：
     *condition.addCondition("enterprise_id",3000007,Condition.EQ).addCondition("create_time","2014-9-16 17:48:02",Condition.GT);
     * @param name     数据库字段名
     * @param value    字段值
     * @param relation 关系符，默认为‘=’， ‘=’ or '<' or '<' or '!=' 等
     */
    public Condition addCondition(String name, Object value, String relation) {
        if (StringUtils.isEmpty(name)) {
            return this;
        }
        if (StringUtils.isEmpty(relation)) {
            relation = "=";
        }
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("name", name);
        m.put("value", value);
        m.put("relation", relation);
        this.conditions.add(m);
        return this;
    }

    /**
     * 设置条件,可以设置任意多个条件，默认条件为等于(=)<br>使用方法：
     * condition.addCondition("enterprise_id",3000007).addCondition("name","张三");
     * @param name  数据库字段名
     * @param value 字段值
     */
    public Condition addCondition(String name, Object value) {
        return addCondition(name, value, EQ);
    }

    public Integer getLimit() {
        return limit;
    }

    public Condition setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getStart() {
        return start;
    }

    public Condition setStart(Integer start) {
        this.start = start;
        return this;
    }

    /**
     * 设置排序规则,如果不指定sortValue，默认为DESC,可以设置任意多个排序规则<br>使用方法：
     * condition.addSort("id", Condition.SORT_DESC).addSort("create_time", Condition.SORT_ASC);
     * @param sortName  需要排序的字段
     * @param sortValue 排序规则 DESC  or ASC
     */
    public Condition addSort(String sortName, String sortValue) {
        if (StringUtils.isEmpty(sortName)) {
            return this;
        }
        if (StringUtils.isEmpty(sortValue)) {
            sortValue = SORT_DESC;
        }
        if (sortBuilder.length()>1) {
			this.sortBuilder.append(" , ");
		}
       this.sortBuilder.append(sortName).append(" ").append(sortValue);
       return this;
    }
    /**
     * 当前查询排序规则
     * @return
     */
    public String getSortBuilder(){
    	return this.sortBuilder.toString();
    }
    /**
     * 指定需要执行的statement，对应mapper.xml里的ID<br>
     * 使用方法：condition.setStatement(MybatisBaseGenericDAOImpl.SQL_SELECT_LIST_BY_CONDITION)
     * @param statement 需要执行的statement 
     */
    public void setStatement(String statement){
    	this.statement=statement;
    }
    /**
     * 当前条件对应的statement
     * @return
     */
	public String getStatement() {
		return statement;
	}
	/**
	 * 清空Condition，包括条件，排序，分页，查询sql等等
	 * @return
	 */
    public Condition clear(){
    	this.limit=null;
    	this.start=null;
    	this.sortBuilder.setLength(0);
    	this.conditions.clear();
    	this.statement=null;
    	return this;
    }
}
