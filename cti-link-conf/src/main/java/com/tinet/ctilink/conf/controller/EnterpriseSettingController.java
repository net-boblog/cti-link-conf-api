package com.tinet.ctilink.conf.controller;

import com.tinet.ctilink.conf.constant.ApiResult;
import com.tinet.ctilink.service.EnterpriseSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping("get")
    public @ResponseBody ApiResult get(@RequestBody String requestBody) {
        System.out.println(requestBody);
//        EnterpriseSettingDto enterpriseSettingDto = enterpriseSettingService.getSettingByName(enterpriseId, name);
//
//        ApiResult apiResult = ApiResultFactory.getRequestSuccess(enterpriseSettingDto);
//        ResponseEntity<ApiResult> responseEntity = new ResponseEntity<ApiResult>(apiResult, HttpStatus.OK);
        return null;
    }
}
