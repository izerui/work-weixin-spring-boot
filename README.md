# 基于 WxJava - 企业微信的第三方服务商应用及自建应用的多租户支持

[![GitHub followers](https://img.shields.io/github/followers/izerui?style=social)](https://github.com/izerui?tab=followers)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.izerui/work-weixin-spring-boot-starter)](https://mvnrepository.com/artifact/io.github.izerui/work-weixin-spring-boot-starter)
[![CircleCI](https://circleci.com/gh/izerui/work-weixin-spring-boot/tree/master.svg?style=svg)](https://circleci.com/gh/izerui/work-weixin-spring-boot/tree/master)

* 基于WxJava（企业微信）进行api调用，支持多租户。
* 扩展支持api调用的时候可以指定以某一个租户配置进行调用。
* 支持服务端集成，远程修改同步租户的配置，例如使用redis的情况下。
* 默认支持`memory`、`redis`两种缓存的实现。
* 支持开放外部回调接口接收微信回调事件。
* 支持动态加载租户配置

集成（使用方式）：

如果使用redis存储需要增加依赖：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

## 2. 自建多应用（租户）集成
* 引入`maven`依赖：
```xml
<dependency>
    <groupId>io.github.izerui</groupId>
    <artifactId>work-weixin-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```
* 加载租户的应用配置
  * 基础配置：
  ```properties
  work.weixin.storage=redis
  # enable listener 是否开启web支持
  work.weixin.listener-enabled=true
  # 非必须，开启微信事件监听，输出地址。建议按实际暴露公网地址配置
  work.weixin.callback-url=https://weixin-dev.yj2025.com
  ```
  * 静态声明租户配置:
    ```properties
    # default cp config
    work.weixin.configs[0].tenant-id=feike
    work.weixin.configs[0].corp-id=ww7c4f40dafaee2f4c
    work.weixin.configs[0].corp-secret=XXX
    work.weixin.configs[0].agent-id=1000014
    work.weixin.configs[0].listener-token=XXX
    work.weixin.configs[0].listener-aes-key=XXX
    
    work.weixin.configs[1].tenant-id=jingguan
    work.weixin.configs[1].corp-id=ww7c4f40dafaee2f4c
    work.weixin.configs[1].corp-secret=XXX
    work.weixin.configs[1].agent-id=1000017
    
    work.weixin.configs[2].tenant-id=xiaochengxu
    work.weixin.configs[2].corp-id=ww7c4f40dafaee2f4c
    work.weixin.configs[2].corp-secret=XXX
    work.weixin.configs[2].agent-id=1000049
    work.weixin.configs[2].listener-token=XXX
    work.weixin.configs[2].listener-aes-key=XXX
    ```

  * 动态租户配置(注册spring bean即可，指定tenantId调用的时候，缓存中没有会自动查找loader-bean进行动态加载)：
    ```java
    @Component
    public class DynamicConfigLoader implements CpConfigLoader {
        @Override
        public WxProperties.CpConfig getConfig(String tenantId) {
            return new WxProperties.CpConfig()
                    .setTenantId("feike")
                    .setCorpId("wx7003aae3ac")
                    .setCorpSecret("f4Q3KJgMnLBxoAik6NmKrcYA26ZEZCkz_f94uQ")
                    .setListenerToken("6HFXyimVN37E5f")
                    .setListenerAesKey("oHhKlG1x37YXFkwg9Ncglm2wfIANxFAGn9")
                    .setAgentId(1000003);
        }
    }
    ```

  * 亦可通过代码手动吸入缓存配置:
    ```java
    @Autowired
    private CpService cpService;
    
            TenantConfigOperator configOperator = cpService.getConfigOperator();
    // 手动配置
            configOperator.setConfigs(...);
    
    // 保存租户对应的配置项
            configOperator.setCorpId(tenantId, "...")
            configOperator.setCorpSecret(tenantId, "...")
    ```

* 接口调用:
```java
@Autowired
private CpService cpService;

public void test() {
    cpService.tenant("feike") // 这里指定租户例如： feike
        .getMessageService()
        .send(message);    
}
```

* 微信事件推送回调（需暴露已集成的服务到公网，并且配置文件中: work.weixin.listener-enabled=true）
```java
@Component
public class MessageListener implements CpListener {

    @Override
    public void listener(String tenantId, WxCpXmlMessage wxMessage, CpService wxCpService) {
        log.info("tenatnId: {} wxMessage: \n{}", BLUE(tenantId), MAGENTA(gson.toJson(wxMessage)));
    }
    
}
```

## 3. 服务商第三方应用集成
* 引入`maven`依赖：
```xml
<dependency>
    <groupId>io.github.izerui</groupId>
    <artifactId>work-weixin-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```
* 声明配置(第三方应用服务商配置只支持通过配置文件声明):
```properties
work.weixin.storage=redis
# enable listener
work.weixin.listener-enabled=true
work.weixin.callback-url=https://weixin-dev.yj2025.com
# tp config 服务商第三方应用相关配置
work.weixin.tp-config.suite-id=ww178ba164679bc532
work.weixin.tp-config.suite-secret=XXX
work.weixin.tp-config.listener-token=XXX
work.weixin.tp-config.listener-aes-key=XXX
work.weixin.tp-config.corp-id=ww7c4f40dafaee2f4c
work.weixin.tp-config.provider-secret=XXX
```
* 企业安装应用后回调事件捕获示例：
```java
public class TenantAuthBindingListener implements AuthBindingListener {


    @Override
    public void listener(String tenantId, WxCpTpPermanentCodeInfo authInfo) {
        // 授权企业id
        String authCorpId = authInfo.getAuthCorpInfo().getCorpId();
        // 授权企业的应用id
        String authAgentId = authInfo.getAuthInfo().getAgents().get(0).getAgentId();
        // 授权企业的永久授权码
        String authPermanentCode = authInfo.getPermanentCode();
        // 尽量保存到企业的持久记录中,续业务自行建立监听器进行处理
        ...
    }
}
```

* 接口调用指定租户后，动态加载租户配置(期初授权企业安装第三方应用后保存的应用信息比如：永久授权码等):
```java
@Component
public class DynamicTpConfigLoader implements TpAuthConfigLoader {
    @Override
    public WxProperties.TpAuthConfig getConfig(String tenantId) {
        DbInfo db = dbDao.findConfig(tenantId);
        return new WxProperties.TpAuthConfig()
                .setTenantId(tenantId)
                .setCorpId(db.getCorpId())
                .setAgentId(db.getAgentId())
                .setPermanentCode(db.getPermanentCode());;
    }
}
```
* 服务商微信事件监听回调:
```java
@Component
public class MessageTpListener implements TpListener {

    @Override
    public void listener(WxCpTpXmlMessage wxMessage, TpService tpService) {
        log.info("wxMessage: \n{}", MAGENTA(gson.toJson(wxMessage)));

        if (wxMessage.getInfoType() != null) {
            switch (wxMessage.getInfoType()) {
                case "suite_ticket":
                    tpService.setSuiteTicket(wxMessage.getSuiteTicket(), properties.getTpConfig().getSuiteTicketExpiresTime());
                    break;
            }
        }

    }
}
```

注： TpService 为服务商接口调用对象，CpService为调用授权企业或者自建应用的接口调用对象。其他使用方式自行翻看源码。