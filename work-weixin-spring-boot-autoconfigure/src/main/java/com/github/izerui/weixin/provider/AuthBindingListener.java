package com.github.izerui.weixin.provider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.chanjar.weixin.cp.bean.WxCpTpPermanentCodeInfo;


/**
 * 企业微信管理员添加第三方应用授权后，页面重定向监听
 */
@FunctionalInterface
public interface AuthBindingListener {
    void listener(String tenantId, WxCpTpPermanentCodeInfo authInfo, HttpServletRequest request, HttpServletResponse response);
}
