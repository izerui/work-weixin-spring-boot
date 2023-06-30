package com.github.izerui.weixin.callback;

import com.github.izerui.weixin.CpService;
import com.github.izerui.weixin.WxProperties;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.izerui.weixin.support.ColorOutput.BLUE;

@Slf4j
@RestController
public class CpCallbackController implements CommandLineRunner {

    @Autowired
    private CpService cpService;
    @Autowired
    private WxCpMessageRouter wxCpMessageRouter;
    @Autowired
    private WxProperties properties;

    @RequestMapping(value = "/message/{tenantId}", produces = "text/html;charset=utf-8")
    public String message(@PathVariable("tenantId") String tenantId,
                          @RequestParam("msg_signature") String msgSignature,
                          @RequestParam("nonce") String nonce,
                          @RequestParam("timestamp") String timestamp,
                          @RequestParam(value = "echostr", required = false) String echostr,
                          HttpServletRequest request) throws IOException {
        try {
            // 必要: 切换配置
            cpService.tenant(tenantId, false);
            // 获取切换后的配置存储对象
            WxCpConfigStorage wxCpConfigStorage = cpService.getWxCpConfigStorage();
            if (StringUtils.isNotBlank(echostr)) {
                if (!this.cpService.checkSignature(msgSignature, timestamp, nonce, echostr)) {
                    // 消息签名不正确，说明不是公众平台发过来的消息
                    return "非法请求";
                }
                WxCpCryptUtil cryptUtil = new WxCpCryptUtil(wxCpConfigStorage);
                String plainText = cryptUtil.decrypt(echostr);
                // 说明是一个仅仅用来验证的请求，回显echostr
                return plainText;
            }

            Map<String, Object> context = new HashMap<>();
            context.put("tenantId", tenantId);

            WxCpXmlMessage inMessage = WxCpXmlMessage
                    .fromEncryptedXml(request.getInputStream(), wxCpConfigStorage, timestamp, nonce, msgSignature);
            WxCpXmlOutMessage outMessage = this.wxCpMessageRouter.route(inMessage, context);
            if (outMessage != null) {
                return outMessage.toEncryptedXml(wxCpConfigStorage);
            }
            return "非法请求";
        } catch (Exception ex) {
            log.error("request: {}", request.getQueryString());
            log.error(ex.getMessage(), ex);
            return "非法请求";
        }
    }


    @Override
    public void run(String... args) throws Exception {
        log.info(BLUE(":::: 自建应用回调地址: " + properties.getCallbackUrl() + "/message/[tenantId]"));
    }
}
