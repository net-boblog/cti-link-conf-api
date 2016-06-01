package com.tinet.ctilink.conf.service.imp;

import com.alibaba.dubbo.config.annotation.Service;
import com.tinet.ctilink.conf.ApiResult;
import com.tinet.ctilink.conf.mapper.EntityMapper;
import com.tinet.ctilink.conf.mapper.EnterpriseIvrMapper;
import com.tinet.ctilink.conf.model.CtiLinkEnterpriseIvrAnchor;
import com.tinet.ctilink.conf.service.v1.CtiLinkEnterpriseIvrAnchorService;
import com.tinet.ctilink.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @author fengwei //
 * @date 16/5/17 09:19
 */
@Service
public class EnterpriseIvrAnchorServiceImp extends BaseService<CtiLinkEnterpriseIvrAnchor> implements CtiLinkEnterpriseIvrAnchorService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private EnterpriseIvrMapper enterpriseIvrMapper;

    @Override
    public ApiResult<CtiLinkEnterpriseIvrAnchor> createEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        //验证enterpriseId
        if (!entityMapper.validateEntity(enterpriseIvrAnchor.getEnterpriseId())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[enterpriseId]不正确");
        }
        enterpriseIvrAnchor.setId(null);
        if (enterpriseIvrAnchor.getIvrId() == null || enterpriseIvrAnchor.getIvrId() < 0) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[ivrId]不正确");
        }
        if (StringUtils.isEmpty(enterpriseIvrAnchor.getPath())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[path]不能为空");
        }
        if (StringUtils.isEmpty(enterpriseIvrAnchor.getEvent())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[event]不正确");
        }
        if (StringUtils.isEmpty(enterpriseIvrAnchor.getData())) {
            return new ApiResult<>(ApiResult.FAIL_RESULT, "参数[data]不能为空");
        }

        Condition condition = new Condition(CtiLinkEnterpriseIvrAnchor.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("", "");

        return null;
    }

    @Override
    public ApiResult deleteEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        return null;
    }

    @Override
    public ApiResult<CtiLinkEnterpriseIvrAnchor> updateEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        return null;
    }

    @Override
    public ApiResult<List<CtiLinkEnterpriseIvrAnchor>> listEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        return null;
    }

    @Override
    public ApiResult<CtiLinkEnterpriseIvrAnchor> getEnterpriseIvrAnchor(CtiLinkEnterpriseIvrAnchor enterpriseIvrAnchor) {
        return null;
    }
}
