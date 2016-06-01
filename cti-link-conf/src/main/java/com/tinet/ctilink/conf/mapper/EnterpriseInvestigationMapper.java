package com.tinet.ctilink.conf.mapper;

import com.tinet.ctilink.conf.model.EnterpriseInvestigation;
import com.tinet.ctilink.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * @author fengwei //
 * @date 16/4/12 15:09
 */
@Component
public interface EnterpriseInvestigationMapper extends BaseMapper<EnterpriseInvestigation> {

    /**
     * 删除满意的调查节点, 需要将所有子节点都删除
     * @param id 节点id
     */
    int deleteRecursive(Integer id);
}
