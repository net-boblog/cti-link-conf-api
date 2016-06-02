package com.tinet.ctilink.conf.mapper;

import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.mapper.BaseMapper;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/12 09:26
 */
@Component
public interface EntityMapper extends BaseMapper<Entity> {

    List<Entity> list();

    boolean validateEntity(Integer enterpriseId);

    Integer generateEnterpriseId();
}
