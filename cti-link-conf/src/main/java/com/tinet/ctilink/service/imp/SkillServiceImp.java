package com.tinet.ctilink.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.ApiResult;
import com.tinet.ctilink.model.Skill;
import com.tinet.ctilink.service.SkillService;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/4/7 17:17
 */
@Service
public class SkillServiceImp implements SkillService {

    @Override
    public ApiResult<Skill> create(Skill skill) {
        return null;
    }

    @Override
    public ApiResult delete(Skill skill) {
        return null;
    }

    @Override
    public ApiResult<Skill> update(Skill skill) {
        return null;
    }

    @Override
    public ApiResult<List<Skill>> list(Skill skill) {
        return null;
    }
}
