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
    private List<EnterpriseHotline> enterpriseHotline;

    //trunk
    //和enterpriseHotline一一对应
    private List<Trunk> trunk;

    //enterprise_clid
    private EnterpriseClid enterpriseClid;

    //enterprise_router
    private EnterpriseRouter enterpriseRouter;


    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<EnterpriseHotline> getEnterpriseHotline() {
        return enterpriseHotline;
    }

    public void setEnterpriseHotline(List<EnterpriseHotline> enterpriseHotline) {
        this.enterpriseHotline = enterpriseHotline;
    }

    public List<Trunk> getTrunk() {
        return trunk;
    }

    public void setTrunk(List<Trunk> trunk) {
        this.trunk = trunk;
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
}
