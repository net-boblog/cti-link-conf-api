package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.EnterpriseInvestigation;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/27 14:43
 */
@Repository
public class EnterpriseInvestigationDao extends BaseDao<EnterpriseInvestigation> {

    private static final String DELETE_BY_SQL = "deleteBySql";

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private RedisService redisService;

    /**
     * 删除满意的调查节点, 需要将所有子节点都删除
     * @param id 节点id
     * @return
     */
    public int deleteEnterpriseInvestigation(Integer id) {
        return sqlSession.delete(DELETE_BY_SQL, id);
    }

    public boolean loadCache() {
        List<Entity> list = entityDao.list();
        for (Entity entity : list) {
            loadCache(entity.getEnterpriseId());
        }
        return true;
    }

    public boolean loadCache(Integer enterpriseId) {
        Condition condition = new Condition(EnterpriseInvestigation.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        List<EnterpriseInvestigation> list = selectByCondition(condition);
        if (list != null) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_INVESTIGATION_ENTERPRISE_ID
                    , enterpriseId), list);
        }
        return true;
    }

    public boolean cleanCache() {

        return true;
    }
}
