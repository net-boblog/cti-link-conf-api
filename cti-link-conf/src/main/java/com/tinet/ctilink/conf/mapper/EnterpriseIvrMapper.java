package com.tinet.ctilink.conf.mapper;

import com.tinet.ctilink.conf.model.EnterpriseIvr;
import com.tinet.ctilink.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * @author fengwei //
 * @date 16/4/12 15:10
 */
@Component
public interface EnterpriseIvrMapper extends BaseMapper<EnterpriseIvr> {

    /**
     * 删除语音导航节点, 需要将所有子节点都删除
     * @param id 节点id
     */
    int deleteRecursive(Integer id);
}
