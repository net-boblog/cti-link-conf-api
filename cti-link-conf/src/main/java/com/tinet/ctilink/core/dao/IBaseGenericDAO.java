package com.tinet.ctilink.core.dao;

import com.tinet.ctilink.core.util.Condition;

import java.io.Serializable;
import java.util.List;


/**
 * 基础DAO接口
 * 
 * @param <T> 业务实体类型
 * @param <ID> ID类型 ，如：String、Long、Integer 等
 */
public interface IBaseGenericDAO<T, ID extends Serializable> {

    /**
     * 保存（持久化）对象
     * @param ob 要持久化的对象
     * @return 执行成功的记录个数
     */
	public Integer insert(T ob);
	
	 /**
     * 保存（持久化）对象,对字段选择性保存，当某字段为null时，此字段不保存
     * @param ob 要持久化的对象
     * @return 执行成功的记录个数
     */
	public Integer insertSelective(T ob);

    /**
     * 更新（持久化）对象
     * @param ob 要持久化的对象
     * @return 执行成功的记录个数
     */
	public Integer updateByPrimaryKey(T ob);
	
    /**
     * 更新（持久化）对象,对字段选择性更新，当某字段为null时，此字段不更新
     * @param ob 要持久化的对象
     * @return 执行成功的记录个数
     */
	public Integer updateByPrimaryKeySelective(T ob);

	/**
	 * 更新
	 * @param statement 映射的语句ID
	 * @param parameter 参数
	 * @return 执行结果——更新成功的记录数
	 * @return
	 */
	public int update(String statement, Object parameter);

    /**
     * 获取指定的唯一标识符对应的持久化对象
     *
     * @param id 指定的唯一标识符
     * @return 指定的唯一标识符对应的持久化对象，如果没有对应的持久化对象，则返回null。
     */
	public T selectByPrimaryKey(ID id);

    /**
     * 删除指定的唯一标识符对应的持久化对象
     *
     * @param id 指定的唯一标识符
	 * @return 删除的对象数量
     */
	public Integer deleteByPrimaryKey(ID id);

    /**
     * 删除指定的唯一标识符数组对应的持久化对象
     *
     * @param ids 指定的唯一标识符数组
	 * @return 删除的对象数量
     */
	public Integer deleteByIds(ID[] ids);

    /**
     * 获取条件下的数量
     * @param condition
     * @return 查询结果分页数据
     */
    public Integer getCountByCondition(Condition condition);

	/**
	 * 获取满足查询参数条件的数据总数
	 * 
	 * @param param 查询参数
	 * @return 数据总数
	 */
	public Integer getCountBy(T param);
	
	/**
	 * 查询结果集,返回多条记录
	 * @param statement 映射的语句ID
	 * @param parameter 参数
	 * @return 查询结果列表
	 */
	public List<T> selectList(String statement, T parameter);
	
	/**返回一条记录
	 * @param statement 映射的语句ID
	 * @param parameter 参数
	 * @return 查询结果对象
	 */
	public Object selectOne(String statement, T parameter);
	
	/**
	 * 查询结果集,返回多条记录
	 * @param statement 映射的语句ID
	 * @param c 参数
	 * @return 查询结果列表
	 */
	public List<T> selectList(String statement, Condition c);
	
    /**
     * @param statement 映射的语句ID
     * @param parameter 参数
     * @return 执行结果——删除成功的记录数
     */
	public int delete(String statement, Object parameter);
	
    /**
     * 查询结果集,返回多条记录
     * @param c
     * @return 查询结果列表
     */
    public List<T> searchListByCondition(Condition c);

}
