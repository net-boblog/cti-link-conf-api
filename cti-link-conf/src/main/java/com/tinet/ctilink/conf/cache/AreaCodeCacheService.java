package com.tinet.ctilink.conf.cache;

import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.model.AreaCode;
import com.tinet.ctilink.inc.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.geom.Area;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/5/9 17:01
 */
@Component
public class AreaCodeCacheService extends AbstractCacheService<AreaCode> {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean reloadCache() {
        //判断是否已经有缓存
        AreaCode ac = redisService.get(Const.REDIS_DB_AREA_CODE_INDEX, String.format(CacheKey.AREA_CODE_PREFIX, "010")
                , AreaCode.class);
        if (ac != null) {
            return true;
        }

        List<AreaCode> areaCodeList = selectAll();

        Map<String, AreaCode> map = new HashMap<String, AreaCode>();
        for (AreaCode areaCode : areaCodeList) {
            map.put(String.format(CacheKey.AREA_CODE_PREFIX, areaCode.getPrefix()), areaCode);
        }
        redisService.multiset(Const.REDIS_DB_AREA_CODE_INDEX, map);
        return true;
    }
}
