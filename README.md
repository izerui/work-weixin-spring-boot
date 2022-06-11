# 基于 WxJava - 企业微信的多租户集成

[![GitHub followers](https://img.shields.io/github/followers/izerui?style=social)](https://github.com/izerui?tab=followers)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.izerui/work-weixin-spring-boot-starter)](https://mvnrepository.com/artifact/io.github.izerui/work-weixin-spring-boot-starter)
[![CircleCI](https://circleci.com/gh/izerui/work-weixin-spring-boot/tree/master.svg?style=svg)](https://circleci.com/gh/izerui/work-weixin-spring-boot/tree/master)

* 基于WxJava（企业微信）进行api调用，支持多租户。
* 扩展支持api调用的时候可以指定以某一个租户配置进行调用。
* 支持服务端集成，远程修改同步租户的配置，例如使用redis的情况下。
* 默认支持`memory`、`redis`两种缓存的实现。

集成（使用方式）：

1. 引入`maven`依赖：
```xml
<dependency>
    <groupId>io.github.izerui</groupId>
    <artifactId>work-weixin-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```
如果使用redis存储需要增加依赖：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2. 通过spring配置文件初始化多租户配置:
```properties
# 企业微信配置
work.weixin.storage=memory
#work.weixin.storage=redis

# 账号配置,多个的话请递增顺序号
work.weixin.configs[0].tenant-id=feike
work.weixin.configs[0].corp-id=wx7004ac2607aae3ac
work.weixin.configs[0].corp-secret=f4QXoH0x5KJgMnLBxoAik6NmKrcYA26ZEZCkz_f94uQ
work.weixin.configs[0].token=6HFXyimVNitD3REk87E5f
work.weixin.configs[0].aes-key=oHhKlG1xj2aEyZM2WC7YXFkwg9Ncglm2wfIANxFAGn9
work.weixin.configs[0].agent-id=1000003
#work.weixin.configs[0].msg-audit-lib-path=
#work.weixin.configs[0].oauth2redirect-uri=
#work.weixin.configs[0].webhook-key=

#spring.redis.database=0
#spring.redis.host=10.96.72.124
#spring.redis.port=6379
#spring.redis.lettuce.pool.max-idle=500
#spring.redis.lettuce.pool.min-idle=50
#spring.redis.lettuce.pool.max-wait=-1s
#spring.redis.lettuce.pool.max-active=-1
```

3. 直接使用：
```java
@Autowired
private TenantWxCpService tenantWxCpService;

tenantWxCpService.tenant(tenantId)
    .getMessageService()
    .send(message);
```

4. 手动、更新配置：
```java
@Autowired
private TenantWxCpService tenantWxCpService;

TenantConfigOperator configOperator = tenantWxCpService.getConfigOperator();
// 手动配置
configOperator.setConfigs(...);

// 保存租户对应的配置项
configOperator.setCorpId(tenantId, "...")
configOperator.setCorpSecret(tenantId, "...")
```

5. 消息回调获取:
通过将tenantId配置到回调地址中，用来获取不同租户的tenantId，进而通过指定租户进行验签。
```java
@RequestMapping(value = "/message/{tenantId}", produces = "text/html;charset=utf-8")
public String message(@PathVariable("tenantId") String tenantId,
                      @RequestParam("msg_signature") String msgSignature,
                      @RequestParam("nonce") String nonce,
                      @RequestParam("timestamp") String timestamp,
                      @RequestParam(value = "echostr",required = false) String echostr,
                      HttpServletRequest request) throws IOException {
    // 必要: 切换当前请求配置
    tenantWxCpService.tenant(tenantId);
    // 获取切换后的配置存储对象
    WxCpConfigStorage wxCpConfigStorage = tenantWxCpService.getWxCpConfigStorage();
    ... ...
    return outMessage.toEncryptedXml(wxCpConfigStorage);
}
```
