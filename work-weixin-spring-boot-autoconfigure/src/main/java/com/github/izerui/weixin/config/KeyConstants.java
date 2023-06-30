package com.github.izerui.weixin.config;

import java.util.function.Function;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
public final class KeyConstants {

    public final static String CORPID_KEY_PREFIX = "work:weixin:corpId:";
    public final static Function<String, String> CORPID_KEY = s -> String.format(CORPID_KEY_PREFIX + "%s", s);
    public final static Function<String, String> CORPSECRET_KEY = s -> String.format("work:weixin:corpSecret:%s", s);
    public final static Function<String, String> TOKEN_KEY = s -> String.format("work:weixin:token:%s", s);
    public final static Function<String, String> ENCODINGAESKEY_KEY = s -> String.format("work:weixin:encodingAESKey:%s", s);
    public final static Function<String, String> AGENTID_KEY = s -> String.format("work:weixin:agentId:%s", s);
    public final static Function<String, String> PERMANENT_CODE_KEY = s -> String.format("work:weixin:permanentCode:%s", s);
    public final static Function<String, String> MSGAUDITLIBPATH_KEY = s -> String.format("work:weixin:msgAuditLibPath:%s", s);
    public final static Function<String, String> JSAPITICKET_KEY = s -> String.format("work:weixin:jsapiTicket:%s", s);
    public final static Function<String, String> AGENTJSAPITICKET_KEY = s -> String.format("work:weixin:agentJsapiTicket:%s", s);
    public final static Function<String, String> ACCESSTOKEN_KEY = s -> String.format("work:weixin:accessToken:%s", s);
    public final static Function<String, String> OAUTH2REDIRECTURI_KEY = s -> String.format("work:weixin:oauth2redirectUri:%s", s);
    public final static Function<String, String> WEBHOOKKEY_KEY = s -> String.format("work:weixin:webhookKey:%s", s);
    public final static Function<String, String> MSG_AUDIT_SECRET_KEY = s -> String.format("work:weixin:msgAuditSecret:%s", s);
    public final static Function<String, String> MSG_AUDIT_PRIKEY_KEY = s -> String.format("work:weixin:msgAuditPriKey:%s", s);
}
