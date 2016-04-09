package com.tinet.ctilink.conf.web.controller;

import com.tinet.ctilink.conf.constant.ApiResult;
import com.tinet.ctilink.conf.constant.ApiResultFactory;
import com.tinet.ctilink.conf.dto.EnterpriseSettingDto;
import com.tinet.ctilink.conf.service.EnterpriseSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fengwei //
 * @date 16/4/8 17:39
 */
@Controller
@RequestMapping("enterprise/setting")
public class EnterpriseSettingController {
    @Autowired
    EnterpriseSettingService enterpriseSettingService;

    //TODO 接收JSON格式参数

    @RequestMapping("get")
    public @ResponseBody ResponseEntity<ApiResult> get(int enterpriseId, String name) {
        //TODO 参数验证

        ApiResult<EnterpriseSettingDto> apiResult = enterpriseSettingService.getSettingByName(enterpriseId, name);

        //JSON格式
        return ApiResultFactory.getResponse(apiResult);
    }

    @RequestMapping("test")
    public @ResponseBody ResponseEntity<ApiResult> test() {

        ApiResult apiResult = enterpriseSettingService.testSetting();
        return ApiResultFactory.getResponse(apiResult);
    }
}
