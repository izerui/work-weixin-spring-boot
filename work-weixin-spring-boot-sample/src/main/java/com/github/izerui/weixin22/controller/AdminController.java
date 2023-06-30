package com.github.izerui.weixin22.controller;

import com.github.izerui.weixin.TpService;
import com.github.izerui.weixin.WxProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "后台调用接口")
@RestController
public class AdminController {

    @Autowired
    private TpService tpService;
    @Autowired
    private WxProperties properties;


    @Operation(summary = "第一步: 生成租户相应的应用安装地址")
    @GetMapping("/admin/install-url")
    public String getPreAuthUrl(@Parameter(name = "租户ID") @RequestParam("tenantId") String tenantId,
                                @Parameter(name = "0:正式、1:测试") @RequestParam("authType") Integer authType) throws WxErrorException {

        String preAuthUrl = tpService.getPreAuthUrl(properties.getCallbackUrl() + "/app/installed", tenantId, 1);
        return preAuthUrl;
    }


    @Operation(summary = "第二步: 激活账号,授权调用许可")
    @GetMapping("/admin/active-user")
    public String activeUserId(@Parameter(name = "激活码") @RequestParam("activeCode") String activeCode,
                               @Parameter(name = "租户ID") @RequestParam("tenantId") String tenantId,
                               @Parameter(name = "账户ID") @RequestParam("authUserId") String authUserId) throws WxErrorException {

        WxProperties.TpAuthConfig authConfig = tpService.getAuthConfig(tenantId);
        tpService.getLicenseService().activeAccount(activeCode, authConfig.getCorpId(), authUserId);
        return "success";
    }

}
