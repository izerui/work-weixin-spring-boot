package com.github.izerui.weixin22;

import com.github.izerui.weixin.WxProperties;
import com.github.izerui.weixin.provider.TpAuthConfigLoader;
import org.springframework.stereotype.Component;

@Component
public class TpAuthAppLoader implements TpAuthConfigLoader {
    @Override
    public WxProperties.TpAuthConfig getConfig(String tenantId) {
        return new WxProperties.TpAuthConfig()
                .setTenantId("yunji-wode")
                .setCorpId("ww7c4f40dafaee2f4c")
                .setAgentId(1000061)
                .setPermanentCode("k6QRaIefAYf3Y_gxy5c1S-83vw8xFi-ZoXgV9MjtuxQ");
    }
}
