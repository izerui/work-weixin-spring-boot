package com.github.izerui.weixin.callback;

import com.github.izerui.weixin.CpService;
import com.github.izerui.weixin.WxProperties;
import com.github.izerui.weixin.provider.AuthBindingListener;
import me.chanjar.weixin.cp.bean.WxCpTpPermanentCodeInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheAuthBindingListener implements AuthBindingListener {

    private CpService cpService;

    public CacheAuthBindingListener(CpService cpService) {
        this.cpService = cpService;
    }

    @Override
    public void listener(String tenantId, WxCpTpPermanentCodeInfo authInfo, HttpServletRequest request, HttpServletResponse response) {
        WxProperties.TpAuthConfig tpAuthConfig = new WxProperties.TpAuthConfig()
                .setTenantId(tenantId)
                .setCorpId(authInfo.getAuthCorpInfo().getCorpId())
                .setAgentId(authInfo.getAuthInfo().getAgents().get(0).getAgentId())
                .setPermanentCode(authInfo.getPermanentCode());
        // 保存到当前缓存中，尽量保存到企业的持久记录中,续业务自行建立监听器进行处理
        cpService.getConfigOperator().setConfigs(tpAuthConfig.toCpConfig());
        String outputContent = String.format("授权成功: 请保存以下内容(并提供给应用服务商)。</br> tenantId: %s </br> 永久授权码: %s </br> 授权人: %s </br> corpid: %s </br> agentid: %s </br>",
                tenantId,
                authInfo.getPermanentCode(),
                authInfo.getAuthUserInfo().getUserId(),
                authInfo.getAuthCorpInfo().getCorpId(),
                authInfo.getAuthInfo().getAgents().get(0).getAgentId());
        try {
            response.getWriter().write(outputContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
