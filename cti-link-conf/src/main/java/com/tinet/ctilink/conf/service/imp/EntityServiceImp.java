package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.cache.CacheKey;
import com.tinet.ctilink.cache.RedisService;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.filter.AfterReturningMethod;
import com.tinet.ctilink.conf.filter.ProviderFilter;
import com.tinet.ctilink.conf.mapper.*;
import com.tinet.ctilink.conf.model.*;
import com.tinet.ctilink.conf.request.EntityCreateRequest;
import com.tinet.ctilink.inc.Const;
import com.tinet.ctilink.json.JSONArray;
import com.tinet.ctilink.json.JSONObject;
import com.tinet.ctilink.service.BaseService;
import com.tinet.ctilink.conf.service.v1.CtiLinkEntityService;
import com.tinet.ctilink.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author fengwei //
 * @date 16/4/7 13:45
 */
@Service
public class EntityServiceImp extends BaseService<Entity>
        implements CtiLinkEntityService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TrunkMapper trunkMapper;

    @Autowired
    private SipGroupMapper sipGroupMapper;

    @Autowired
    private RoutersetMapper routersetMapper;

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private EnterpriseClidMapper enterpriseClidMapper;

    @Autowired
    private EnterpriseRouterMapper enterpriseRouterMapper;

    @Autowired
    private EnterpriseHotlineMapper enterpriseHotlineMapper;

    @Autowired
    private EnterpriseSettingMapper enterpriseSettingMapper;

    @Autowired
    private RedisService redisService;

    //开户
    @Override
    public ApiResult<EntityCreateRequest> createEntity(EntityCreateRequest entityCreateRequest) {
        Entity entity = entityCreateRequest.getEntity();
        EnterpriseClid enterpriseClid = entityCreateRequest.getEnterpriseClid();
        List<EnterpriseHotline> enterpriseHotlineList = entityCreateRequest.getEnterpriseHotlineList();
        List<Trunk> trunkList = entityCreateRequest.getTrunkList();
        EnterpriseRouter enterpriseRouter = entityCreateRequest.getEnterpriseRouter();

        //检验参数
        //entity
        ApiResult<EntityCreateRequest> result = validateEntity(entity);
        if (result != null) {
            return result;
        }
        //router
        result = validateRouter(enterpriseRouter);
        if (result != null) {
            return result;
        }
        //clid
        result = validateClid(enterpriseClid);
        if (result != null) {
            return result;
        }
        //enterprise_hotline trunk
        result = validateHotline(enterpriseHotlineList, trunkList);
        if (result != null) {
            return result;
        }

        //先插入 entity, 生成enterpriseId
        Integer enterpriseId = entityMapper.generateEnterpriseId();
        Date createTime = new Date();
        entity.setEnterpriseId(enterpriseId);
        entity.setCreateTime(createTime);

        enterpriseClid.setEnterpriseId(enterpriseId);
        enterpriseClid.setCreateTime(createTime);

        for (EnterpriseHotline enterpriseHotline : enterpriseHotlineList) {
            enterpriseHotline.setEnterpriseId(enterpriseId);
            enterpriseHotline.setCreateTime(createTime);
        }

        for (Trunk trunk : trunkList) {
            trunk.setEnterpriseId(enterpriseId);
            trunk.setCreateTime(createTime);
        }

        enterpriseRouter.setEnterpriseId(enterpriseId);
        enterpriseRouter.setCreateTime(createTime);

        //入库
        insertSelective(entity);

        enterpriseClidMapper.insertSelective(enterpriseClid);

        enterpriseRouterMapper.insertSelective(enterpriseRouter);

        for (EnterpriseHotline enterpriseHotline : enterpriseHotlineList) {
            enterpriseHotlineMapper.insertSelective(enterpriseHotline);
        }

        for (Trunk trunk : trunkList) {
            trunkMapper.insertSelective(trunk);
        }

        // enterprise_setting, 插入默认值
        EnterpriseSetting enterpriseSetting = new EnterpriseSetting();
        enterpriseSetting.setEnterpriseId(enterpriseId);
        enterpriseSetting.setName("");
        enterpriseSetting.setValue("");
        enterpriseSetting.setProperty("");
        enterpriseSettingMapper.insertSelective(enterpriseSetting);

        //更新缓存
        setRefreshCacheMethod("setCache", entity);
        return new ApiResult<>(entityCreateRequest);
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

    private <T> ApiResult<T> validateEntity(Entity entity) {
        if (StringUtils.isEmpty(entity.getAreaCode())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[entity.areaCode]不正确");
        }

        if (!entity.getAreaCode().matches(Const.AREA_CODE_VALIDATION)) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[entity.areaCode]格式不正确");
        }
        if (StringUtils.isEmpty(entity.getEnterpriseName())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[entity.enterpriseName]不正确");
        }
        entity.setEnterpriseName(SqlUtil.escapeSql(entity.getEnterpriseName()));

        if (entity.getEntityType() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[entity.entityType]不正确");
        }

        if (entity.getStatus() == null || entity.getStatus() < 0 || entity.getStatus() > 3) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[entity.status]不正确");
        }

        return null;
    }

    private <T> ApiResult<T> validateRouter(EnterpriseRouter enterpriseRouter) {
        if (enterpriseRouter.getIbRouterRight() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[ibRouterRight]不能为空");
        }
        if (enterpriseRouter.getObPredictiveRouterLeft() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPredictiveRouterLeft]不能为空");
        }
        if (enterpriseRouter.getObPreviewRouterLeft() == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPreviewRouterLeft]不能为空");
        }

        //判断是否在routerset中
        Condition condition = new Condition(Routerset.class);
        Condition.Criteria criteria = condition.createCriteria();
        List<Integer> routersetIdList = new ArrayList<>();
        routersetIdList.add(enterpriseRouter.getIbRouterRight());
        routersetIdList.add(enterpriseRouter.getObPredictiveRouterLeft());
        routersetIdList.add(enterpriseRouter.getObPreviewRouterLeft());
        criteria.andIn("id", routersetIdList);
        List<Routerset> routersetList = routersetMapper.selectByCondition(condition);
        int flag = 0;
        for (Routerset routerset : routersetList) {
            if (routerset.getId().equals(enterpriseRouter.getIbRouterRight())) {
                flag = flag | 1;
            }
            if (routerset.getId().equals(enterpriseRouter.getObPredictiveRouterLeft())) {
                flag = flag | 2;
            }
            if (routerset.getId().equals(enterpriseRouter.getObPreviewRouterLeft())) {
                flag = flag | 4;
            }
        }
        if (flag != 7) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数不正确");
        }
        return null;
    }

    private <T> ApiResult<T> validateClid(EnterpriseClid enterpriseClid) {
        //判断类型, 1中继  2客户  3固定  4热线
        if (enterpriseClid.getIbClidRightType() <=0 || enterpriseClid.getIbClidRightType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[ibClidRightType]不正确");
        }
        if (enterpriseClid.getObPredictiveClidLeftType() <= 0 || enterpriseClid.getObPredictiveClidLeftType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPredictiveClidLeftType]不正确");
        }
        if (enterpriseClid.getObPredictiveClidRightType() <= 0 || enterpriseClid.getObPredictiveClidRightType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPredictiveClidRightType]不正确");
        }
        if (enterpriseClid.getObPreviewClidLeftType() <= 0 || enterpriseClid.getObPreviewClidLeftType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPreviewClidLeftType]不正确");
        }
        if (enterpriseClid.getObPreviewClidRightType() <= 0 || enterpriseClid.getObPreviewClidRightType() > 4) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[obPreviewClidRightType]不正确");
        }

        //判断号码
        if (enterpriseClid.getIbClidRightType() == 1) {

        }
        return null;
    }

    private <T> ApiResult<T> validateHotline(List<EnterpriseHotline> enterpriseHotlineList, List<Trunk> trunkList) {
        if (trunkList == null) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[trunkList]不能为空");
        }

        int type1Count = 0;
        for (int i = 0; i < trunkList.size(); i++) {
            Trunk trunk = trunkList.get(i);
            if (trunk.getType() == 0) {  //未绑定400号

            } else if (trunk.getType() == 1) {  //绑定了400号, enterprise_hotline里面需要有
                type1Count++;
                if (!isExistEnterpriseHotline(trunk.getNumberTrunk(), enterpriseHotlineList)) {
                    return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseHotlineList]不正确");
                }
            } else if (trunk.getType() == 2) {  //手机虚拟号码

            }
        }

        //enterpriseHotlineList的数量, 应该和trunk的type=1的一致
        if (type1Count != enterpriseHotlineList.size()) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[trunkList]不正确");
        }

        return null;
    }

    private boolean isExistEnterpriseHotline(String numberTrunk, List<EnterpriseHotline> enterpriseHotlineList) {
        for (EnterpriseHotline enterpriseHotline : enterpriseHotlineList) {
            if (enterpriseHotline.getNumberTrunk().equals(numberTrunk)) {
                return true;
            }
        }
        return false;
    }

    //缓存更新
    public void setCache(Entity entity) {
        //entity
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTITY_ENTERPRISE_ID
                , entity.getEnterpriseId()), entity);
        redisService.set(Const.REDIS_DB_CONF_INDEX, CacheKey.ENTITY_ENTERPRISE_ACTIVE, entityMapper.list());

        //enterprise_router
        Condition condition = new Condition(EnterpriseRouter.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("enterpriseId", entity.getEnterpriseId());
        List<EnterpriseRouter> enterpriseRouterList = enterpriseRouterMapper.selectByCondition(condition);
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_ROUTER_ENTERPRISE_ID
                , entity.getEnterpriseId()), enterpriseRouterList.get(0));

        //enterprsie_clid
        Condition condition1 = new Condition(EnterpriseClid.class);
        Condition.Criteria criteria1 = condition1.createCriteria();
        criteria1.andEqualTo("enterpriseId", entity.getEnterpriseId());
        List<EnterpriseClid> enterpriseClidList = enterpriseClidMapper.selectByCondition(condition1);
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_CLID_ENTERPRISE_ID
                , entity.getEnterpriseId()), enterpriseClidList.get(0));

        //enterprise_hotline
        Condition condition2 = new Condition(EnterpriseHotline.class);
        Condition.Criteria criteria2 = condition2.createCriteria();
        criteria2.andEqualTo("enterpriseId", entity.getEnterpriseId());
        condition2.setOrderByClause("is_master desc");
        List<EnterpriseHotline> enterpriseHotlineList = enterpriseHotlineMapper.selectByCondition(condition2);
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_HOTLINE_ENTERPRISE_ID
                , entity.getEnterpriseId()), enterpriseHotlineList);
        for (EnterpriseHotline enterpriseHotline : enterpriseHotlineList) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_HOTLINE_ENTERPRISE_ID_NUMBER_TRUNK
                    , entity.getEnterpriseId(), enterpriseHotline.getNumberTrunk()), enterpriseHotline);
        }

        //trunk
        Condition condition3 = new Condition(Trunk.class);
        Condition.Criteria criteria3 = condition3.createCriteria();
        criteria3.andEqualTo("enterpriseId", entity.getEnterpriseId());
        List<Trunk> trunkList = trunkMapper.selectByCondition(condition3);
        //.first
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.TRUNK_ENTERPRISE_ID_FIRST
                , entity.getEnterpriseId()), trunkList.get(0));
        //.number_trunk
        redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.TRUNK_ENTERPRISE_ID
                , entity.getEnterpriseId()), trunkList);

        //enterprise_setting
        Condition condition4 = new Condition(EnterpriseSetting.class);
        Condition.Criteria criteria4 = condition4.createCriteria();
        criteria4.andEqualTo("enterpriseId", entity.getEnterpriseId());
        List<EnterpriseSetting> enterpriseSettingList = enterpriseSettingMapper.selectByCondition(condition4);
        for (EnterpriseSetting enterpriseSetting : enterpriseSettingList) {
            redisService.set(Const.REDIS_DB_CONF_INDEX, String.format(CacheKey.ENTERPRISE_SETTING_ENTERPRISE_ID_NAME
                    , enterpriseSetting.getEnterpriseId(), enterpriseSetting.getName()), enterpriseSetting);
        }
    }

    private void setRefreshCacheMethod(String methodName, Entity entity) {
        try {
            Method method = this.getClass().getMethod(methodName, Entity.class);
            AfterReturningMethod afterReturningMethod = new AfterReturningMethod(method, this, entity);
            ProviderFilter.LOCAL_METHOD.set(afterReturningMethod);
        } catch (Exception e) {
            logger.error("EntityServiceImp.setRefreshCacheMethod error, cache refresh fail, " +
                    "class=" + this.getClass().getName(), e);
        }
    }
}
