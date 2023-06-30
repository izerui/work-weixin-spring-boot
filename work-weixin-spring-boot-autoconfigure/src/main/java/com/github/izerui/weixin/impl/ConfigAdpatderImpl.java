package com.github.izerui.weixin.impl;

import com.github.izerui.weixin.ConfigStorageAdpatder;
import com.github.izerui.weixin.WxProperties;
import com.github.izerui.weixin.provider.CpConfigLoader;
import com.github.izerui.weixin.provider.TpAuthConfigLoader;
import com.github.izerui.weixin.config.ConfigOperator;
import lombok.Getter;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.constant.WxCpApiPathConsts;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.Assert;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

/**
 * cp适配器
 *
 * @author liuyuhua
 * @date 2022/4/18
 */
@ThreadSafe
public class ConfigAdpatderImpl implements ConfigStorageAdpatder {

    // 当前线程使用的tenantId(公司编号)
    protected final static InheritableThreadLocal<String> INHERITABLE_THREAD_ACTIVE_TENANT_ID = new InheritableThreadLocal<String>();
    // 当前tenantId对应的是否是第三方app
    protected final static InheritableThreadLocal<Boolean> INHERITABLE_THREAD_ACTIVE_TENANT_TYPE = new InheritableThreadLocal<Boolean>();

    @Getter
    protected ConfigOperator configOperator;
    @Getter
    protected WxProperties properties;
    protected ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders;
    private ObjectProvider<CpConfigLoader> cpConfigLoaders;
    private ObjectProvider<TpAuthConfigLoader> tpAuthConfigLoaders;

    public ConfigAdpatderImpl(ConfigOperator configOperator,
                              WxProperties properties,
                              ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders,
                              ObjectProvider<CpConfigLoader> cpConfigLoaders,
                              ObjectProvider<TpAuthConfigLoader> tpAuthConfigLoaders) {
        this.configOperator = configOperator;
        this.properties = properties;
        this.apacheHttpClientBuilders = apacheHttpClientBuilders;
        this.cpConfigLoaders = cpConfigLoaders;
        this.tpAuthConfigLoaders = tpAuthConfigLoaders;
    }

    /**
     * 当前请求线程切换使用的租户配置
     *
     * @param tenantId
     * @param isThirdApp
     * @return
     */
    public ConfigAdpatderImpl tenant(String tenantId, boolean isThirdApp) {
        INHERITABLE_THREAD_ACTIVE_TENANT_ID.set(tenantId);
        INHERITABLE_THREAD_ACTIVE_TENANT_TYPE.set(isThirdApp);
        if (!configOperator.isExistConfig(tenantId, isThirdApp)) {
            WxProperties.CpConfig t = getIfNotExists(tenantId, isThirdApp);
            if (t == null) {
                throw new RuntimeException("无法获取tenantId:[" + tenantId + "]相应的配置");
            }
            configOperator.setConfigs(t);
        }
        return this;
    }

    /**
     * 当前请求的tenantId对应的是否是第三方应用
     *
     * @return
     */
    public boolean isThirdApp() {
        Boolean aBoolean = INHERITABLE_THREAD_ACTIVE_TENANT_TYPE.get();
        Assert.notNull(aBoolean, "未指定租户信息,请调用 cpService.tenantId([租户ID], [是否是第三方应用])");
        return aBoolean;
    }

    public String tenantId() {
        String tenantId = INHERITABLE_THREAD_ACTIVE_TENANT_ID.get();
        Assert.notNull(tenantId, "未指定租户信息,请调用 cpService.tenantId([租户ID], [是否是第三方应用])");
        return tenantId;
    }

    @Override
    public String getPermanentCode() {
        return configOperator.getPermanentCode(tenantId());
    }

    @Override
    public void deleteTenantConfig() {
        configOperator.deleteConfig(tenantId());
    }

    @Deprecated
    public void setBaseApiUrl(String baseUrl) {
        throw new UnsupportedOperationException();
    }

    public String getApiUrl(String path) {
        return WxCpApiPathConsts.DEFAULT_CP_BASE_URL + path;
    }

    private WxProperties.CpConfig getIfNotExists(String tenantId, boolean isThirdApp) {
        AtomicReference<WxProperties.CpConfig> config = new AtomicReference<>();
        if (isThirdApp) {
            tpAuthConfigLoaders.ifAvailable(loader -> {
                WxProperties.TpAuthConfig tpAuthConfig = loader.getConfig(tenantId);
                if (tpAuthConfig != null) {
                    config.set(tpAuthConfig.toCpConfig());
                }
            });
        } else {
            cpConfigLoaders.ifAvailable(loader -> {
                WxProperties.CpConfig cpConfig = loader.getConfig(tenantId);
                if (cpConfig != null) {
                    config.set(cpConfig);
                }
            });
        }
        return config.get();
    }

    @Override
    public String getAccessToken() {
        return configOperator.getAccessToken(tenantId());
    }

    @Override
    public Lock getAccessTokenLock() {
        return configOperator.getAccessTokenLock(tenantId());
    }

    @Override
    public boolean isAccessTokenExpired() {
        return configOperator.isAccessTokenExpired(tenantId());
    }

    @Override
    public void expireAccessToken() {
        configOperator.expireAccessToken(tenantId());
    }

    @Override
    public void updateAccessToken(WxAccessToken accessToken) {
        configOperator.updateAccessToken(tenantId(), accessToken);
    }

    @Override
    public void updateAccessToken(String accessToken, int expiresIn) {
        WxAccessToken wat = new WxAccessToken();
        wat.setAccessToken(accessToken);
        wat.setExpiresIn(expiresIn);
        configOperator.updateAccessToken(tenantId(), wat);
    }

    @Override
    public String getJsapiTicket() {
        return configOperator.getJsapiTicket(tenantId());
    }

    @Override
    public Lock getJsapiTicketLock() {
        return configOperator.getJsapiTicketLock(tenantId());
    }

    @Override
    public boolean isJsapiTicketExpired() {
        return configOperator.isJsapiTicketExpired(tenantId());
    }

    @Override
    public void expireJsapiTicket() {
        configOperator.expireJsapiTicket(tenantId());
    }

    @Override
    public void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        configOperator.setJsapiTicket(tenantId(), jsapiTicket, expiresInSeconds);
    }

    @Override
    public String getAgentJsapiTicket() {
        return configOperator.getAgentJsapiTicket(tenantId());
    }

    @Override
    public Lock getAgentJsapiTicketLock() {
        return configOperator.getAgentJsapiTicketLock(tenantId());
    }

    @Override
    public boolean isAgentJsapiTicketExpired() {
        return configOperator.isAgentJsapiTicketExpired(tenantId());
    }

    @Override
    public void expireAgentJsapiTicket() {
        configOperator.expireAgentJsapiTicket(tenantId());
    }

    @Override
    public void updateAgentJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        configOperator.updateAgentJsapiTicket(tenantId(), jsapiTicket, expiresInSeconds);
    }

    @Override
    public String getCorpId() {
        return configOperator.getCorpId(tenantId());
    }

    @Override
    public String getCorpSecret() {
        return configOperator.getCorpSecret(tenantId());
    }

    @Override
    public Integer getAgentId() {
        Integer agentId = configOperator.getAgentId(tenantId());
        if (agentId != null) {
            return Integer.valueOf(agentId);
        }
        return null;
    }

    @Override
    public String getToken() {
        return configOperator.getToken(tenantId());
    }

    @Override
    public String getAesKey() {
        return configOperator.getAesKey(tenantId());
    }

    @Override
    public String getMsgAuditLibPath() {
        return configOperator.getMsgAuditLibPath(tenantId());
    }

    @Override
    public long getExpiresTime() {
        return configOperator.getExpiresTime(tenantId());
    }

    @Override
    public String getOauth2redirectUri() {
        return configOperator.getOauth2redirectUri(tenantId());
    }

    @Override
    public String getHttpProxyHost() {
        return properties.getProxy().getHttpProxyHost();
    }

    @Override
    public int getHttpProxyPort() {
        return properties.getProxy().getHttpProxyPort();
    }

    @Override
    public String getHttpProxyUsername() {
        return properties.getProxy().getHttpProxyUsername();
    }

    @Override
    public String getHttpProxyPassword() {
        return properties.getProxy().getHttpProxyPassword();
    }

    @Override
    public File getTmpDirFile() {
        return properties.getTmpDirFile();
    }

    @Override
    public ApacheHttpClientBuilder getApacheHttpClientBuilder() {
        return apacheHttpClientBuilders.getIfAvailable();
    }

    @Override
    public boolean autoRefreshToken() {
        return true;
    }

    @Override
    public String getWebhookKey() {
        return configOperator.getWebhookKey(tenantId());
    }

    @Override
    public String getMsgAuditPriKey() {
        return configOperator.getMsgAuditPriKey(tenantId());
    }

    @Override
    public String getMsgAuditSecret() {
        return configOperator.getMsgAuditSecret(tenantId());
    }

}
