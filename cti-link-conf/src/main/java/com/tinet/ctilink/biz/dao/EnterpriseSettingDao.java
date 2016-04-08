package com.tinet.ctilink.biz.dao;

import com.tinet.ctilink.biz.model.EnterpriseSetting;
import com.tinet.ctilink.core.dao.IBaseGenericDAO;

/**
 * @author fengwei //
 * @date 16/4/8 10:20
 */
public interface EnterpriseSettingDao extends IBaseGenericDAO<EnterpriseSetting, Long> {

    EnterpriseSetting selectByName(Integer enterpriseId, String name);
}
