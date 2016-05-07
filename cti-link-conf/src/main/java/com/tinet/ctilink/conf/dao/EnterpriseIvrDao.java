package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.EnterpriseIvr;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/27 16:50
 */
@Repository
public class EnterpriseIvrDao extends BaseDao<EnterpriseIvr> {

    private static final String DELETE_BY_SQL = "deleteBySql";

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private RedisService redisService;

    /**
     * 删除语音导航节点, 需要将所有子节点都删除
     * @param id 节点id
     * @return
     */
    public int deleteEnterpriseIvr(Integer id) {
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
        Condition condition = new Condition(EnterpriseIvr.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("ivr_id");
        List<EnterpriseIvr> list = selectByCondition(condition);
        if (list == null) {
            return true;
        }

        int ivrId = -1;
        List<EnterpriseIvr> subList = new ArrayList<>();
        for (EnterpriseIvr enterpriseIvr : list) {
            if (ivrId == -1) {
                ivrId = enterpriseIvr.getIvrId();
            }
            if (ivrId == enterpriseIvr.getIvrId()) {
                subList.add(enterpriseIvr);
            }
            if (ivrId != enterpriseIvr.getIvrId()) {
                redisService.set(Const.REDIS_DB_CONF_INDEX
                        , String.format(CacheKey.ENTERPRISE_IVR_ENTERPRISE_ID_IVR_ID, enterpriseId
                        , ivrId), subList);

                ivrId = enterpriseIvr.getIvrId();
                subList = new ArrayList<>();
                subList.add(enterpriseIvr);
            }
        }

        if (subList.size() > 0) {
            redisService.set(Const.REDIS_DB_CONF_INDEX
                    , String.format(CacheKey.ENTERPRISE_IVR_ENTERPRISE_ID_IVR_ID, enterpriseId
                    , ivrId), subList);
        }
        return true;
    }

    public boolean cleanCache() {

        return true;
    }
}
