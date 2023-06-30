package com.github.izerui.weixin.config;

import com.github.izerui.weixin.WxProperties;
import me.chanjar.weixin.common.bean.WxAccessToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author liuyuhua
 * @date 2022/4/19
 */
public abstract class AbstractConfigOperator implements ConfigOperator {

    protected WxProperties properties;

    protected transient Map<String, Lock> tenantAccessTokenLock = new HashMap<>();
    protected transient Map<String, Lock> tenantJsapiTicketLock = new HashMap<>();
    protected transient Map<String, Lock> tenantAgentJsapiTicketLock = new HashMap<>();

    public AbstractConfigOperator(WxProperties properties) {
        this.properties = properties;
    }

    /**
     * 通过key获取值
     *
     * @param key
     * @return
     */
    protected abstract String get(String key);

    /**
     * 持久化一个kv
     *
     * @param key
     * @param value
     */
    protected abstract void set(String key, String value);

    /**
     * 持久化kv并设置失效时长（秒）
     *
     * @param key
     * @param value
     * @param expiredSeconds
     */
    protected abstract void set(String key, String value, int expiredSeconds);

    /**
     * 移除一个key
     *
     * @param key
     */
    protected abstract void remove(String key);

    /**
     * 判断一个key是否存在
     *
     * @param key
     * @return
     */
    protected abstract boolean exist(String key);

    /**
     * 获取一个key的到期时长
     *
     * @param key
     * @return
     */
    protected abstract long getExpiredSeconds(String key);

    // config
    @Override
    public void setConfigs(WxProperties.CpConfig... configs) {
        for (WxProperties.CpConfig config : configs) {
            this.setCorpId(config.getTenantId(), config.getCorpId());
            this.setCorpSecret(config.getTenantId(), config.getCorpSecret());
            this.setToken(config.getTenantId(), config.getListenerToken());
            this.setAesKey(config.getTenantId(), config.getListenerAesKey());
            this.setAgentId(config.getTenantId(), config.getAgentId());
            this.setPermanentCode(config.getTenantId(), config.getPermanentCode());
            this.setMsgAuditLibPath(config.getTenantId(), config.getMsgAuditLibPath());
            this.setWebhookKey(config.getTenantId(), config.getWebhookKey());
            this.setOauth2redirectUri(config.getTenantId(), config.getOauth2redirectUri());
        }
    }

    @Override
    public WxProperties.CpConfig getConfig(String tenantId) {
        return new WxProperties.CpConfig()
                .setTenantId(tenantId)
                .setCorpId(getCorpId(tenantId))
                .setCorpSecret(getCorpSecret(tenantId))
                .setListenerToken(getToken(tenantId))
                .setListenerAesKey(getAesKey(tenantId))
                .setAgentId(getAgentId(tenantId))
                .setPermanentCode(getPermanentCode(tenantId))
                .setMsgAuditLibPath(getMsgAuditLibPath(tenantId))
                .setWebhookKey(getWebhookKey(tenantId));
    }

    @Override
    public List<WxProperties.CpConfig> getConfigs() {
        return getTenantIds().stream().map(tenantId -> getConfig(tenantId)).collect(Collectors.toList());
    }

    @Override
    public abstract Set<String> getTenantIds();

    @Override
    public void deleteConfig(String tenantId) {
        this.remove(KeyConstants.CORPID_KEY.apply(tenantId));
        this.remove(KeyConstants.CORPSECRET_KEY.apply(tenantId));
        this.remove(KeyConstants.TOKEN_KEY.apply(tenantId));
        this.remove(KeyConstants.ENCODINGAESKEY_KEY.apply(tenantId));
        this.remove(KeyConstants.AGENTID_KEY.apply(tenantId));
        this.remove(KeyConstants.PERMANENT_CODE_KEY.apply(tenantId));
        this.remove(KeyConstants.MSGAUDITLIBPATH_KEY.apply(tenantId));
        this.remove(KeyConstants.WEBHOOKKEY_KEY.apply(tenantId));
        this.remove(KeyConstants.OAUTH2REDIRECTURI_KEY.apply(tenantId));
    }

    @Override
    public boolean isExistConfig(String tenantId, boolean isThirdApp) {
        String corpId = getCorpId(tenantId);
        Integer agentId = getAgentId(tenantId);
        if (isThirdApp) {
            String permanentCode = getPermanentCode(tenantId);
            return corpId != null && permanentCode != null && agentId != null;
        }
        String corpSecret = getCorpSecret(tenantId);
        return corpId != null && corpSecret != null && agentId != null;
    }

    @Override
    public String getCorpId(String tenantId) {
        return get(KeyConstants.CORPID_KEY.apply(tenantId));
    }

    @Override
    public String getCorpSecret(String tenantId) {
        return get(KeyConstants.CORPSECRET_KEY.apply(tenantId));
    }

    @Override
    public String getToken(String tenantId) {
        return get(KeyConstants.TOKEN_KEY.apply(tenantId));
    }

    @Override
    public String getAesKey(String tenantId) {
        return get(KeyConstants.ENCODINGAESKEY_KEY.apply(tenantId));
    }

    @Override
    public Integer getAgentId(String tenantId) {
        String s = get(KeyConstants.AGENTID_KEY.apply(tenantId));
        if (s == null) {
            return null;
        }
        return Integer.valueOf(s);
    }

    @Override
    public String getPermanentCode(String tenantId) {
        return get(KeyConstants.PERMANENT_CODE_KEY.apply(tenantId));
    }

    @Override
    public void setPermanentCode(String tenantId, String permanentCode) {
        set(KeyConstants.PERMANENT_CODE_KEY.apply(tenantId), permanentCode);
    }

    @Override
    public String getMsgAuditLibPath(String tenantId) {
        return get(KeyConstants.MSGAUDITLIBPATH_KEY.apply(tenantId));
    }

    @Override
    public void setCorpId(String tenantId, String corpId) {
        set(KeyConstants.CORPID_KEY.apply(tenantId), corpId);
    }

    @Override
    public void setCorpSecret(String tenantId, String corpSecret) {
        set(KeyConstants.CORPSECRET_KEY.apply(tenantId), corpSecret);
    }

    @Override
    public void setToken(String tenantId, String token) {
        set(KeyConstants.TOKEN_KEY.apply(tenantId), token);
    }

    @Override
    public void setAesKey(String tenantId, String encodingAESKey) {
        set(KeyConstants.ENCODINGAESKEY_KEY.apply(tenantId), encodingAESKey);
    }

    @Override
    public void setAgentId(String tenantId, Integer agentId) {
        set(KeyConstants.AGENTID_KEY.apply(tenantId), agentId == null ? null : String.valueOf(agentId));
    }

    @Override
    public void setMsgAuditLibPath(String tenantId, String msgAuditLibPath) {
        set(KeyConstants.MSGAUDITLIBPATH_KEY.apply(tenantId), msgAuditLibPath);
    }

    @Override
    public String getWebhookKey(String tenantId) {
        return get(KeyConstants.WEBHOOKKEY_KEY.apply(tenantId));
    }

    @Override
    public void setWebhookKey(String tenantId, String webhookKey) {
        set(KeyConstants.WEBHOOKKEY_KEY.apply(tenantId), webhookKey);
    }

    @Override
    public String getOauth2redirectUri(String tenantId) {
        return get(KeyConstants.OAUTH2REDIRECTURI_KEY.apply(tenantId));
    }

    @Override
    public void setOauth2redirectUri(String tenantId, String oauth2redirectUri) {
        set(KeyConstants.OAUTH2REDIRECTURI_KEY.apply(tenantId), oauth2redirectUri);
    }


    // runtime
    @Override
    public boolean isAgentJsapiTicketExpired(String tenantId) {
        return !exist(KeyConstants.AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void expireAgentJsapiTicket(String tenantId) {
        remove(KeyConstants.AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void updateAgentJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
        set(KeyConstants.AGENTJSAPITICKET_KEY.apply(tenantId), jsapiTicket, expiresInSeconds);
    }

    @Override
    public void updateAccessToken(String tenantId, WxAccessToken accessToken) {
        set(KeyConstants.ACCESSTOKEN_KEY.apply(tenantId), accessToken.getAccessToken(), accessToken.getExpiresIn());
    }

    @Override
    public String getAccessToken(String tenantId) {
        return get(KeyConstants.ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public long getExpiresTime(String tenantId) {
        return getExpiredSeconds(KeyConstants.ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public boolean isJsapiTicketExpired(String tenantId) {
        return !exist(KeyConstants.JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void expireJsapiTicket(String tenantId) {
        remove(KeyConstants.JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public String getJsapiTicket(String tenantId) {
        return get(KeyConstants.JSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public void setJsapiTicket(String tenantId, String jsapiTicket, int expiresInSeconds) {
        set(KeyConstants.JSAPITICKET_KEY.apply(tenantId), jsapiTicket, expiresInSeconds);
    }

    @Override
    public String getAgentJsapiTicket(String tenantId) {
        return get(KeyConstants.AGENTJSAPITICKET_KEY.apply(tenantId));
    }

    @Override
    public boolean isAccessTokenExpired(String tenantId) {
        return !exist(KeyConstants.ACCESSTOKEN_KEY.apply(tenantId));
    }

    @Override
    public void expireAccessToken(String tenantId) {
        remove(KeyConstants.ACCESSTOKEN_KEY.apply(tenantId));
    }

    private Lock getLockIfAvailable(String tenantId, Map<String, Lock> map) {
        Lock lock = map.get(tenantId);
        if (lock == null) {
            lock = new ReentrantLock();
            map.put(tenantId, lock);
        }
        return lock;
    }


    @Override
    public Lock getAccessTokenLock(String tenantId) {
        return getLockIfAvailable(tenantId, tenantAccessTokenLock);
    }

    @Override
    public Lock getJsapiTicketLock(String tenantId) {
        return getLockIfAvailable(tenantId, tenantJsapiTicketLock);
    }


    @Override
    public Lock getAgentJsapiTicketLock(String tenantId) {
        return getLockIfAvailable(tenantId, tenantAgentJsapiTicketLock);
    }

    @Override
    public String getMsgAuditSecret(String tenantId) {
        return get(KeyConstants.MSG_AUDIT_SECRET_KEY.apply(tenantId));
    }

    @Override
    public String getMsgAuditPriKey(String tenantId) {
        return get(KeyConstants.MSG_AUDIT_PRIKEY_KEY.apply(tenantId));
    }
}
