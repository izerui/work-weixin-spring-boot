package com.github.izerui;

import com.github.izerui.weixin.CpService;
import com.github.izerui.weixin.WxProperties;
import com.github.izerui.weixin.config.ConfigOperator;
import com.github.izerui.weixin22.Application;
import com.google.common.collect.Lists;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import me.chanjar.weixin.cp.bean.templatecard.HorizontalContent;
import me.chanjar.weixin.cp.bean.templatecard.TemplateCardJump;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;

/**
 * 自建应用测试
 *
 * @author liuyuhua
 * @date 2022/4/19
 */
@SpringBootTest(classes = Application.class)
public class TenantTests {

    @Autowired
    private CpService cpService;


    private void sendDemoMessage(String tenantId) throws WxErrorException {
        WxCpMessage message = new WxCpMessage();
//    message.setAgentId(configStorage.getAgentId());
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        message.setToUser("serv");
        message.setContent("11111欢迎欢迎，热烈欢迎\n换行测试\n超链接:<a href=\"http://www.baidu.com\">Hello World</a>");
        WxCpMessageSendResult messageSendResult = cpService.tenant(tenantId, false).getMessageService().send(message);
        System.out.println(messageSendResult.toString());
    }

    /**
     * 测试读取spring 配置的多租户信息进行发送测试
     *
     * @throws WxErrorException
     */
    @Test
    public void testSpring() throws WxErrorException {
        this.sendDemoMessage("k8s-local");
    }

    @Test
    public void testTemplateMessage() throws WxErrorException {
        WxCpMessage reply = WxCpMessage.TEMPLATECARD().toUser("serv")
                .cardType(WxConsts.TemplateCardType.TEXT_NOTICE)
                .sourceIconUrl("http://www.yunji2025.com/_nuxt/img/logo.27aea34.png")
                .sourceDesc("服务发布")
                .mainTitleTitle("admin-server 发布成功")
                .mainTitleDesc(new Date().toString())
                .horizontalContents(Arrays.asList(
                        HorizontalContent.builder()
                                .keyname("集群")
                                .value("local")
                                .build(),
                        HorizontalContent.builder()
                                .keyname("环境")
                                .value("test")
                                .build(),
                        HorizontalContent.builder()
                                .keyname("镜像版本")
                                .value("UIHJDNSFIU")
                                .build()))
                .jumps(Lists.newArrayList(
                        TemplateCardJump.builder()
                                .type(1)
                                .title("进入我的的经管")
                                .url("https://yj2025.com")
                                .build()
                ))
                .cardActionType(1)
                .cardActionUrl("https://yj2025.com")
                .build();
        WxCpMessageSendResult yunji = cpService.tenant("k8s-local", false).getMessageService().send(reply);
        System.out.println(yunji.toString());
    }


    /**
     * 手动初始化多租户配置，并测试发送
     *
     * @throws WxErrorException
     */
    @Test
    public void testManualInitConfig() throws WxErrorException {
        ConfigOperator configOperator = cpService.getConfigOperator();
        configOperator.setConfigs(
                new WxProperties.CpConfig()
                        .setTenantId("feike")
                        .setCorpId("wx7003aae3ac")
                        .setCorpSecret("f4Q3KJgMnLBxoAik6NmKrcYA26ZEZCkz_f94uQ")
                        .setListenerToken("6HFXyimVN37E5f")
                        .setListenerAesKey("oHhKlG1x37YXFkwg9Ncglm2wfIANxFAGn9")
                        .setAgentId(1000003)
        );
        this.sendDemoMessage("feike");
    }

    /**
     * 测试调用bean修改其中某个租户的配置，并发送
     *
     * @throws WxErrorException
     */
    @Test
    public void testUpdateConfig() throws WxErrorException {
        ConfigOperator tenantOperator = cpService.getConfigOperator();
        tenantOperator.setCorpSecret("k8s-local", "xxx");
        this.sendDemoMessage("k8s-local");
    }

}
