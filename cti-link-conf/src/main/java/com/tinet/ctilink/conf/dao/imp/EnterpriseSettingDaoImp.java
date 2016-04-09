package com.tinet.ctilink.conf.dao.imp;

import com.tinet.ctilink.conf.dao.EnterpriseSettingDao;
import com.tinet.ctilink.conf.model.EnterpriseSetting;
import com.tinet.ctilink.core.dao.imp.MybatisBaseGenericDAOImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/4/8 10:20
 */
@Repository
public class EnterpriseSettingDaoImp extends MybatisBaseGenericDAOImpl<EnterpriseSetting, Long>
        implements EnterpriseSettingDao {

    private final static String SELECT_BY_NAME = "selectByName";

    @Autowired
    public EnterpriseSettingDaoImp(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public EnterpriseSetting selectByName(int enterpriseId, String name) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("enterpriseId", enterpriseId);
        filter.put("name", name);

        return this.getSqlSession().selectOne(this.getSqlName(SELECT_BY_NAME), filter);
    }
}
