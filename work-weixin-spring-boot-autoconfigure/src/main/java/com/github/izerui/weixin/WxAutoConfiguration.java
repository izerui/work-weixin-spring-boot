package com.github.izerui.weixin;

import com.github.izerui.weixin.callback.WxCallbackConfiguration;
import com.github.izerui.weixin.config.ConfigOperator;
import com.github.izerui.weixin.config.memory.MemoryConfigOperator;
import com.github.izerui.weixin.config.redis.RedisConfigOperator;
import com.github.izerui.weixin.impl.ConfigAdpatderImpl;
import com.github.izerui.weixin.impl.CpServiceImpl;
import com.github.izerui.weixin.impl.TpServiceImpl;
import com.github.izerui.weixin.impl.WxErrorHandler;
import com.github.izerui.weixin.provider.CpConfigLoader;
import com.github.izerui.weixin.provider.TpAuthConfigLoader;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpTpDefaultConfigImpl;
import me.chanjar.weixin.cp.config.impl.WxCpTpRedissonConfigImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Proxy;

/**
 * @author liuyuhua
 * @date 2022/4/18
 */
@EnableConfigurationProperties(WxProperties.class)
@Configuration
@Import(WxCallbackConfiguration.class)
public class WxAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 企业微信自建应用配置适配器
     *
     * @param tenantOperator
     * @param properties
     * @param apacheHttpClientBuilders
     * @param cpConfigLoaders
     * @param authConfigLoaders
     * @return
     */
    @Bean
    public ConfigStorageAdpatder cpConfigStorageAdpatder(ConfigOperator tenantOperator,
                                                         WxProperties properties,
                                                         ObjectProvider<ApacheHttpClientBuilder> apacheHttpClientBuilders,
                                                         ObjectProvider<CpConfigLoader> cpConfigLoaders,
                                                         ObjectProvider<TpAuthConfigLoader> authConfigLoaders) {
        return new ConfigAdpatderImpl(tenantOperator, properties, apacheHttpClientBuilders, cpConfigLoaders, authConfigLoaders);
    }


    @Bean
    public CpService cpService(WxCpConfigStorage cpConfigStorageAdpatder,
                               @Lazy TpService tpService,
                               WxProperties properties) {
        CpServiceImpl wxCpService = new CpServiceImpl();
        wxCpService.setTpService(tpService);
        wxCpService.setProperties(properties);
        wxCpService.setWxCpConfigStorage(cpConfigStorageAdpatder);
        int maxRetryTimes = properties.getMaxRetryTimes();
        if (maxRetryTimes < 0) {
            maxRetryTimes = 0;
        }
        int retrySleepMillis = properties.getRetrySleepMillis();
        if (retrySleepMillis < 0) {
            retrySleepMillis = 1000;
        }
        wxCpService.setRetrySleepMillis(retrySleepMillis);
        wxCpService.setMaxRetryTimes(maxRetryTimes);
        return (CpService) Proxy.newProxyInstance(wxCpService.getClass().getClassLoader(),
                wxCpService.getClass().getInterfaces(),
                new WxErrorHandler(wxCpService, applicationContext));
    }

    @Bean
    public TpService tpService(WxCpTpConfigStorage tpConfigStorage,
                               @Lazy CpService cpService,
                               WxProperties properties) {
        TpServiceImpl tpService = new TpServiceImpl();
        tpService.setWxCpTpConfigStorage(tpConfigStorage);
        tpService.setCpService(cpService);
        tpService.setProperties(properties);
        int maxRetryTimes = properties.getMaxRetryTimes();
        if (maxRetryTimes < 0) {
            maxRetryTimes = 0;
        }
        int retrySleepMillis = properties.getRetrySleepMillis();
        if (retrySleepMillis < 0) {
            retrySleepMillis = 1000;
        }
        tpService.setRetrySleepMillis(retrySleepMillis);
        tpService.setMaxRetryTimes(maxRetryTimes);
        return (TpService) Proxy.newProxyInstance(tpService.getClass().getClassLoader(),
                tpService.getClass().getInterfaces(),
                new WxErrorHandler(tpService, applicationContext));
    }


    @ConditionalOnProperty(value = "work.weixin.storage", matchIfMissing = true, havingValue = "memory")
    @Configuration
    public static class MemoryOperator {


        @Bean
        public MemoryConfigOperator memoryConfigOperator(WxProperties properties) {
            return new MemoryConfigOperator(properties);
        }

        @Bean
        public WxCpTpConfigStorage tpConfigStorage(WxProperties properties) {
            WxProperties.TpConfig tpConfig = properties.getTpConfig();
            WxCpTpDefaultConfigImpl config = new WxCpTpDefaultConfigImpl();
            config.setSuiteAccessTokenExpiresTime(tpConfig.getSuiteAccessTokenExpiresTime());
            config.setSuiteTicketExpiresTime(tpConfig.getSuiteTicketExpiresTime());
            config.setSuiteId(tpConfig.getSuiteId());
            config.setSuiteSecret(tpConfig.getSuiteSecret());
            config.setToken(tpConfig.getListenerToken());
            config.setAesKey(tpConfig.getListenerAesKey());
            config.setOauth2redirectUri(tpConfig.getOauth2redirectUri());
            config.setTmpDirFile(properties.getTmpDirFile());
            return config;
        }

    }

    @ConditionalOnProperty(value = "work.weixin.storage", havingValue = "redis")
    @Configuration
    public static class RedisOperator {

        @Bean
        public RedisConfigOperator redisConfigOperator(StringRedisTemplate redisTemplate,
                                                       WxProperties properties) {
            return new RedisConfigOperator(properties, redisTemplate);
        }

        @Bean
        public WxCpTpConfigStorage wxCpTpConfigStorage(StringRedisTemplate redisTemplate,
                                                       WxProperties properties) {
            WxProperties.TpConfig tpConfig = properties.getTpConfig();
            WxCpTpRedissonConfigImpl config = WxCpTpRedissonConfigImpl.builder()
                    .suiteId(tpConfig.getSuiteId())
                    .suiteSecret(tpConfig.getSuiteSecret())
                    .token(tpConfig.getListenerToken())
                    .aesKey(tpConfig.getListenerAesKey())
                    .corpId(tpConfig.getCorpId())
                    .providerSecret(tpConfig.getProviderSecret())
                    .keyPrefix("work:weixin-tp")
                    .wxRedisOps(new RedisTemplateWxRedisOps(redisTemplate)).build();
            return config;
        }


    }

}
