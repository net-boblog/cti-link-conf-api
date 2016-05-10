package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.dao.EntityDao;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.conf.model.QueueMember;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.*;

/**
 * @author fengwei //
 * @date 16/5/9 18:03
 */
@Component
public class QueueMemberCacheService extends AbstractCacheService<QueueMember> {
    @Autowired
    private RedisService redisService;

    @Autowired
    private EntityDao entityDao;

    @Override
    public boolean reloadCache() {
        List<Entity> list = entityDao.list();
        if (list == null || list.isEmpty()) {
            return true;
        }
        Set<String> qnoDbKeySet = new HashSet<>();
        Set<String> qnoCnoDbKeySet = new HashSet<>();
        Set<String> cnoDbKeySet = new HashSet<>();

        for (Entity entity : list) {
            reloadCache(entity.getEnterpriseId(), qnoDbKeySet, qnoCnoDbKeySet, cnoDbKeySet);
        }

        Set<String> qnoExistKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_QNO.replaceFirst("%d", "%s"), "*", "*"));
        qnoExistKeySet.removeAll(qnoDbKeySet);
        if (qnoExistKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, qnoExistKeySet);
        }

        Set<String> qnoCnoExistKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_QNO_CNO.replaceFirst("%d", "%s"), "*", "*", "*"));
        qnoCnoExistKeySet.removeAll(qnoCnoDbKeySet);
        if (qnoCnoExistKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, qnoCnoExistKeySet);
        }

        Set<String> cnoExistKeySet = redisService.scan(Const.REDIS_DB_CONF_INDEX
                , String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_CNO.replaceFirst("%d", "%s"), "*", "*"));
        cnoExistKeySet.removeAll(cnoDbKeySet);
        if (cnoExistKeySet.size() > 0) {
            redisService.delete(Const.REDIS_DB_CONF_INDEX, cnoExistKeySet);
        }
        return true;
    }


    public boolean reloadCache(Integer enterpriseId, Set<String> qnoDbKeySet, Set<String> qnoCnoDbKeySet
            , Set<String> cnoDbKeySet) {
        Condition condition = new Condition(QueueMember.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);
        condition.setOrderByClause("qno, cno");
        List<QueueMember> list = selectByCondition(condition);
        String key;
        String qno = null;
        Map<String, List<QueueMember>> cnoQueueMap = new HashMap<>();
        List<QueueMember> subList = new ArrayList<>();
        for (QueueMember queueMember : list) {
            key = String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_QNO_CNO, enterpriseId, queueMember.getQno(), queueMember.getCno());
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, queueMember);
            qnoCnoDbKeySet.add(key);

            if (qno == null) {
                qno = queueMember.getQno();
            }
            if (qno.equals(queueMember.getQno())) {
                subList.add(queueMember);
            }
            if (!qno.equals(queueMember.getQno())) {
                key = String.format(CacheKey.QUEUE_ENTERPRISE_ID_QNO, enterpriseId, qno);
                redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
                qnoDbKeySet.add(key);

                qno = queueMember.getQno();
                subList = new ArrayList<>();
                subList.add(queueMember);
            }
            List<QueueMember> cnoQueueMemberList = new ArrayList<>();
            if (cnoQueueMap.containsKey(queueMember.getCno())) {
                cnoQueueMemberList = cnoQueueMap.get(queueMember.getCno());
            }
            cnoQueueMemberList.add(queueMember);
            cnoQueueMap.put(queueMember.getCno(), cnoQueueMemberList);
        }
        if (subList.size() > 0) {
            key = String.format(CacheKey.QUEUE_ENTERPRISE_ID_QNO, enterpriseId, qno);
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, subList);
            qnoDbKeySet.add(key);
        }

        for (Map.Entry<String, List<QueueMember>> entry : cnoQueueMap.entrySet()) {
            key = String.format(CacheKey.QUEUE_MEMBER_ENTERPRISE_ID_CNO, enterpriseId, entry.getKey());
            redisService.set(Const.REDIS_DB_CONF_INDEX, key, entry.getValue());
            cnoDbKeySet.add(key);
        }
        return true;
    }
}
