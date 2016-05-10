package com.tinet.ctilink.conf.dao;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.model.Trunk;
import com.tinet.ctilink.dao.BaseDao;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/6 13:22
 */
@Repository
public class TrunkDao extends BaseDao<Trunk> {

}
