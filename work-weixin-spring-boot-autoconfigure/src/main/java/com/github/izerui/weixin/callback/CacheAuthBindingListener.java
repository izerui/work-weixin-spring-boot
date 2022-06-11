package com.github.izerui.weixin.callback;

import com.github.izerui.weixin.CpService;
import com.github.izerui.weixin.WxProperties;
import com.github.izerui.weixin.provider.AuthBindingListener;
import me.chanjar.weixin.cp.bean.WxCpTpPermanentCodeInfo;

public class CacheAuthBindingListener implements AuthBindingListener {

    private CpService cpService;

    public CacheAuthBindingListener(CpService cpService) {
        this.cpService = cpService;
    }

    @Override
    public void listener(String tenantId, WxCpTpPermanentCodeInfo authInfo) {
        WxProperties.TpAuthConfig tpAuthConfig = new WxProperties.TpAuthConfig()
                .setTenantId(tenantId)
                .setCorpId(authInfo.getAuthCorpInfo().getCorpId())
                .setAgentId(authInfo.getAuthInfo().getAgents().get(0).getAgentId())
                .setPermanentCode(authInfo.getPermanentCode());
        // 保存到当前缓存中，尽量保存到企业的持久记录中,续业务自行建立监听器进行处理
        cpService.getConfigOperator().setConfigs(tpAuthConfig.toCpConfig());
    }
}
