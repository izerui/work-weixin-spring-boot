package com.github.izerui.weixin.config;

import com.github.izerui.weixin.WxProperties;
import me.chanjar.weixin.common.bean.WxAccessToken;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public interface ConfigOperator {

    /**
     * 设置租户的相关配置
     *
     * @param configs
     */
    void setConfigs(WxProperties.CpConfig... configs);

    /**
     * 根据租户ID获取对应的配置
     *
     * @param tenantId
     * @return
     */
    WxProperties.CpConfig getConfig(String tenantId);

    /**
     * 删除指定tenantId对应的配置
     *
     * @param tenantId
     */
    void deleteConfig(String tenantId);

    /**
     * 是否存在当前租户对应的配置
     *
     * @param tenantId
     * @return
     */
    boolean isExistConfig(String tenantId, boolean isThirdApp);

    /**
     * 微信企业号 corpId
     *
     * @param tenantId
     * @return
     */
    String getCorpId(String tenantId);

    /**
     * 微信企业号 corpSecret
     */
    String getCorpSecret(String tenantId);

    /**
     * 微信企业号应用 token
     */
    String getToken(String tenantId);

    /**
     * 微信企业号应用 EncodingAESKey
     */
    String getAesKey(String tenantId);

    /**
     * 微信企业号应用 ID
     */
    Integer getAgentId(String tenantId);

    /**
     * 获取第三方应用授权的永久授权码
     *
     * @param tenantId
     * @return
     */
    String getPermanentCode(String tenantId);

    /**
     * 设置第三方应用授权的永久授权码
     *
     * @param tenantId
     * @param permanentCode
     */
    void setPermanentCode(String tenantId, String permanentCode);

    /**
     * 微信企业号应用 会话存档类库路径
     */
    String getMsgAuditLibPath(String tenantId);

    /**
     * 微信企业号 corpId
     */
    void setCorpId(String tenantId, String corpId);

    /**
     * 微信企业号 corpSecret
     */
    void setCorpSecret(String tenantId, String corpSecret);

    /**
     * 微信企业号应用 token
     */
    void setToken(String tenantId, String token);

    /**
     * 微信企业号应用 EncodingAESKey
     */
    void setAesKey(String tenantId, String encodingAESKey);

    /**
     * 微信企业号应用 ID
     */
    void setAgentId(String tenantId, Integer agentId);

    /**
     * 微信企业号应用 会话存档类库路径
     */
    void setMsgAuditLibPath(String tenantId, String msgAuditLibPath);

    String getWebhookKey(String tenantId);

    void setWebhookKey(String tenantId, String webhookKey);

    String getOauth2redirectUri(String tenantId);

    void setOauth2redirectUri(String tenantId, String oauth2redirectUri);

    Lock getAccessTokenLock(String tenantId);

    boolean isAccessTokenExpired(String tenantId);

    void expireAccessToken(String tenantId);

    Lock getJsapiTicketLock(String tenantId);

    boolean isJsapiTicketExpired(String tenantId);

    void expireJsapiTicket(String tenantId);

    String getJsapiTicket(String tenantId);

    void setJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds);

    String getAgentJsapiTicket(String tenantId);

    Lock getAgentJsapiTicketLock(String tenantId);

    boolean isAgentJsapiTicketExpired(String tenantId);

    void expireAgentJsapiTicket(String tenantId);

    void updateAgentJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds);

    void updateAccessToken(String tenantId, WxAccessToken accessToken);

    String getAccessToken(String tenantId);

    long getExpiresTime(String tenantId);

    List<WxProperties.CpConfig> getConfigs();

    Set<String> getTenantIds();

    String getMsgAuditSecret(String tenantId);

    String getMsgAuditPriKey(String tenantId);
}
