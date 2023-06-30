package com.github.izerui.weixin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
@Data
@ConfigurationProperties(prefix = "work.weixin")
public class WxProperties {

    /**
     * 微信回调的域名地址
     */
    private String callbackUrl = "";

    /**
     * 自建应用配置(支持多应用) 与 tpConfig 二选一
     */
    private List<CpConfig> configs;

    /**
     * 第三方平台应用配置(服务商配置) 与 configs 二选一
     */
    private TpConfig tpConfig = new TpConfig();

    /**
     * 是否开启监听回调
     */
    private boolean listenerEnabled = false;

    /**
     * 缓存类型
     */
    private StorageType storage = StorageType.memory;
    /**
     * 代理服务器配置
     */
    private HttpProxy proxy = new HttpProxy();
    /**
     * # 最大重试次数，默认：5 次，如果小于 0，则为 0
     */
    private Integer maxRetryTimes = 5;
    /**
     * # 重试时间间隔步进，默认：1000 毫秒，如果小于 0，则为 1000
     */
    private Integer retrySleepMillis = 1000;

    private File tmpDirFile;

    @Data
    public static class HttpProxy {
        private String httpProxyHost;
        private Integer httpProxyPort = 0;
        private String httpProxyUsername;
        private String httpProxyPassword;
    }

    public enum StorageType {
        memory, redis;
    }

    @Data
    @NoArgsConstructor(force = true)
    @Accessors(chain = true)
    @ToString
    public static class TpConfig implements Serializable {
        /**
         * suiteId 服务商平台第三方应用页面查看
         */
        @Nonnull
        private String suiteId;
        /**
         * 第三方应用密钥 服务商平台-应用管理页面查看
         */
        @Nonnull
        private String suiteSecret;
        /**
         * 企微服务商企业ID 服务商平台-服务商信息页面查看
         */
        private String corpId;
        /**
         * 第三方应用的token，用来检查应用的签名 服务商平台-应用管理页面-回调配置栏查看
         */
        private String listenerToken;
        /**
         * 第三方应用的EncodingAESKey，用来检查签名 服务商平台-应用管理页面-回调配置栏查看
         */
        private String listenerAesKey;
        /**
         * 服务商secret 查看页面: https://open.work.weixin.qq.com/wwopen/developer#/sass/power/inter
         */
        private String providerSecret;
        /**
         * 下单人
         */
        private String buyUserId = "serv";
        private JsSdkVerify jsSdkVerify = new JsSdkVerify();
        private int providerTokenExpiresTime = 7200;
        private int suiteAccessTokenExpiresTime = 7200;
        private int suiteTicketExpiresTime = 1680; // 28分钟过期
        private String oauth2redirectUri;

        @Data
        public static class JsSdkVerify {
            private String verifyTxtPath = "WW_verify_Rd0su22ZohsSXlGI.txt";
            private String verifyContent = "Rd0su22ZohsSXlGI";
        }
    }

    @Data
    @NoArgsConstructor(force = true)
    @Accessors(chain = true)
    @ToString
    public static class CpConfig implements Serializable {
        @Nonnull
        private String tenantId;
        /**
         * 企业ID 我的企业页面查看
         */
        @Nonnull
        private String corpId;
        /**
         * 应用密钥 我的应用页面查看
         */
        private String corpSecret;
        /**
         * 应用id 我的应用页面查看
         */
        @Nonnull
        private Integer agentId;
        /**
         * 第三方应用的时候使用
         */
        private String permanentCode;
        /**
         * 回调token 启用api接收页面查看
         */
        private String listenerToken;
        /**
         * 回调aeskey 启用api接收页面查看
         */
        private String listenerAesKey;
        private String msgAuditLibPath;
        private String oauth2redirectUri;
        private String webhookKey;
    }

    @Data
    @NoArgsConstructor(force = true)
    @Accessors(chain = true)
    @ToString
    public static class TpAuthConfig implements Serializable {
        @Nonnull
        private String tenantId;
        /**
         * 授权企业的id
         */
        @Nonnull
        private String corpId;
        /**
         * 添加的第三方应用在授权企业下的id
         */
        private Integer agentId;
        /**
         * 授权企业的永久授权码
         */
        @Nonnull
        private String permanentCode;

        public CpConfig toCpConfig() {
            return new WxProperties.CpConfig()
                    .setCorpId(getCorpId())
                    .setAgentId(getAgentId())
                    .setPermanentCode(getPermanentCode())
                    .setTenantId(getTenantId());
        }

        public static TpAuthConfig fromCpConfig(CpConfig cpConfig) {
            return new TpAuthConfig()
                    .setTenantId(cpConfig.getTenantId())
                    .setCorpId(cpConfig.getCorpId())
                    .setPermanentCode(cpConfig.getPermanentCode())
                    .setAgentId(cpConfig.getAgentId());
        }
    }

}
