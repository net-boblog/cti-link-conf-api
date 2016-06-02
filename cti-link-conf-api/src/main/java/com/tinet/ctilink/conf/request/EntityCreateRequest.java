package com.tinet.ctilink.conf.request;

import com.tinet.ctilink.conf.model.*;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/31 14:20
 */
public class EntityCreateRequest {
    //entity
    private Entity entity;

    //enterprise_hotline
    //必须有一个主热线号码
    private List<EnterpriseHotline> enterpriseHotlineList;

    //trunk
    //和enterpriseHotline一一对应
    private List<Trunk> trunkList;

    //enterprise_clid
    private EnterpriseClid enterpriseClid;

    //enterprise_router
    private EnterpriseRouter enterpriseRouter;

    //enterprise_setting
    private List<EnterpriseSetting> enterpriseSettingList;


    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<EnterpriseHotline> getEnterpriseHotlineList() {
        return enterpriseHotlineList;
    }

    public void setEnterpriseHotlineList(List<EnterpriseHotline> enterpriseHotlineList) {
        this.enterpriseHotlineList = enterpriseHotlineList;
    }

    public List<Trunk> getTrunkList() {
        return trunkList;
    }

    public void setTrunkList(List<Trunk> trunkList) {
        this.trunkList = trunkList;
    }

    public EnterpriseClid getEnterpriseClid() {
        return enterpriseClid;
    }

    public void setEnterpriseClid(EnterpriseClid enterpriseClid) {
        this.enterpriseClid = enterpriseClid;
    }

    public EnterpriseRouter getEnterpriseRouter() {
        return enterpriseRouter;
    }

    public void setEnterpriseRouter(EnterpriseRouter enterpriseRouter) {
        this.enterpriseRouter = enterpriseRouter;
    }

    public List<EnterpriseSetting> getEnterpriseSettingList() {
        return enterpriseSettingList;
    }

    public void setEnterpriseSettingList(List<EnterpriseSetting> enterpriseSettingList) {
        this.enterpriseSettingList = enterpriseSettingList;
    }
}
