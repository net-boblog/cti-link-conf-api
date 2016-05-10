package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.conf.model.EnterpriseInvestigation;
import com.tinet.ctilink.dao.BaseDao;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author fengwei //
 * @date 16/4/27 14:43
 */
@Repository
public class EnterpriseInvestigationDao extends BaseDao<EnterpriseInvestigation> {

    private static final String DELETE_BY_SQL = "deleteBySql";

    @Autowired
    private SqlSession sqlSession;

    /**
     * 删除满意的调查节点, 需要将所有子节点都删除
     * @param id 节点id
     * @return
     */
    public int deleteEnterpriseInvestigation(Integer id) {
        return sqlSession.delete(DELETE_BY_SQL, id);
    }

}
