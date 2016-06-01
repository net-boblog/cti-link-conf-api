package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.SipGroupMapper;
import com.tinet.ctilink.conf.mapper.TrunkMapper;
import com.tinet.ctilink.conf.model.SipGroup;
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

    @Autowired
    private SipGroupMapper sipGroupMapper;

    @Autowired
    private EntityMapper entityMapper;

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
        List<Trunk> trunkList = trunkMapper.selectSipGroupList();
        JSONArray jsonArray = new JSONArray();
        for (Trunk trunk : trunkList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("enterpriseId", trunk.getEnterpriseId());
            jsonObject.put("sipGroupId", trunk.getSipGroupId());
            jsonArray.add(jsonObject);
        }
        return new ApiResult<>(jsonArray);
    }

    @Override
    public ApiResult updateEntitySipGroup(Map<String, Integer> params) {
        Integer enterpriseId = params.get("enterpriseId");
        Integer sipGroupId = params.get("sipGroupId");
        if (!entityMapper.validateEntity(enterpriseId)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        //判断sipGroupId是否存在
        if (sipGroupId != -1) {
            SipGroup sipGroup = sipGroupMapper.selectByPrimaryKey(sipGroupId);
            if (sipGroup == null) {
                return new ApiResult(ApiResult.FAIL_RESULT, "参数[sipGroupId]不正确");
            }
        }
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
