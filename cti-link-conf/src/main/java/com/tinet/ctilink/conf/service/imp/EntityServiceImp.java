package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.mapper.TrunkMapper;
import com.tinet.ctilink.conf.model.Trunk;
import com.tinet.ctilink.conf.request.EntityCreateRequest;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.conf.model.Entity;
import com.tinet.ctilink.json.JSONArray;
import com.tinet.ctilink.json.JSONObject;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.conf.service.v1.CtiLinkEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;
import java.util.Map;

/**
 * @author fengwei //
 * @date 16/4/7 13:45
 */
@Service
public class EntityServiceImp extends BaseService<Entity>
        implements CtiLinkEntityService {

    @Autowired
    private TrunkMapper trunkMapper;

    //开户
    @Override
    public ApiResult<EntityCreateRequest> createEntity(EntityCreateRequest entityCreateRequest) {
        return null;
    }

    //更新
    @Override
    public ApiResult<Entity> updateEntity(Entity entity) {
        return null;
    }

    @Override
    public ApiResult<List<Entity>> listEntity() {
        Condition condition = new Condition(Entity.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andNotEqualTo("status", Const.ENTITY_STATUS_CLOSE);
        List<Entity> list = selectByCondition(condition);

        return new ApiResult<>(list);
    }

    @Override
    public ApiResult<Entity> getEntity(Entity entity) {
        if (entity.getEnterpriseId() == null || entity.getEnterpriseId() <= 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        Condition condition = new Condition(Entity.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", entity.getEnterpriseId());
        criteria.andNotEqualTo("status", Const.ENTITY_STATUS_CLOSE);
        List<Entity> list = selectByCondition(condition);
        Entity e = null;
        if (list != null) {
            e = list.get(0);
        }
        return new ApiResult<>(e);
    }

    @Override
    public ApiResult<JSONArray> listEntitySipGroup() {
        Condition condition = new Condition(Trunk.class);
        Condition.Criteria criteria = condition.createCriteria();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        return null;
    }

    @Override
    public ApiResult updateEntitySipGroup(Map<String, Integer> params) {
        Integer enterpriseId = params.get("enterpriseId");
        Integer sipGroupId = params.get("sipGroupId");

        Condition condition = new Condition(Trunk.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", enterpriseId);

        //更新sip_group_id
        Trunk trunk = new Trunk();
        trunk.setSipGroupId(sipGroupId);
        trunkMapper.updateByConditionSelective(trunk, condition);

        return new ApiResult(ApiResult.SUCCESS_RESULT);
    }

}
