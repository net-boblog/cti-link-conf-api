package com.tinet.ctilink.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/**
 * @author fengwei //
 * @date 16/4/12 13:11
 */
@Service
public class RedisService<T> {

    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public boolean set(String key, T value) {
        try {
            String jsonStr = mapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonStr);
        } catch (JsonProcessingException e) {
            logger.error("CacheService.set error, key=" + key + " value=" + value, e);
            return false;
        }

        return true;
    }

    public boolean delete(Collection<String> keySet) {
        redisTemplate.delete(keySet);
        return true;
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
}
