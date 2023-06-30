package com.github.izerui.weixin.callback;

import com.github.izerui.weixin.TpService;
import com.github.izerui.weixin.WxProperties;
import com.github.izerui.weixin.provider.AuthBindingListener;
import com.github.izerui.weixin.support.ColorOutput;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpTpPermanentCodeInfo;
import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.tp.message.WxCpTpMessageRouter;
import me.chanjar.weixin.cp.util.crypto.WxCpTpCryptUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.izerui.weixin.support.ColorOutput.BLUE;
import static com.github.izerui.weixin.support.ColorOutput.YELLOW;

@Slf4j
@RestController
public class TpCallbackController implements CommandLineRunner {

    @Autowired
    private TpService tpService;
    @Autowired
    private WxCpTpMessageRouter tpMessageRouter;
    @Autowired
    private WxProperties.TpConfig.JsSdkVerify jsSdkVerify;
    @Autowired
    private WxProperties properties;
    @Autowired
    private ObjectProvider<AuthBindingListener> authBindingListeners;

    /**
     * 读取配置暴露 可信域名配置地址
     *
     * @return
     */
    @GetMapping("#{jsSdkVerify.verifyTxtPath}")
    public String verifyTxtPath() {
        return jsSdkVerify.getVerifyContent();
    }

    @RequestMapping(value = "/message", produces = "text/html;charset=utf-8")
    public String message(@RequestParam("msg_signature") String msgSignature,
                          @RequestParam("nonce") String nonce,
                          @RequestParam("timestamp") String timestamp,
                          @RequestParam(value = "echostr", required = false) String echostr,
                          HttpServletRequest request) {
        try {
            // 获取切换后的配置存储对象
            WxCpTpConfigStorage tpConfigStorage = tpService.getWxCpTpConfigStorage();
            WxCpTpCryptUtil cryptUtil = new WxCpTpCryptUtil(tpConfigStorage);
            if (StringUtils.isNotBlank(echostr)) {
                if (!this.tpService.checkSignature(msgSignature, timestamp, nonce, echostr)) {
                    // 消息签名不正确，说明不是公众平台发过来的消息
                    return "非法请求";
                }
                String plainText = cryptUtil.decrypt(echostr);
                // 说明是一个仅仅用来验证的请求，回显echostr
                return plainText;
            }

            Map<String, Object> context = new HashMap<>();

            String plainText = cryptUtil.decryptXml(msgSignature, timestamp, nonce, IOUtils.toString(request.getInputStream()));
            log.debug("解密后的原始xml消息内容：{}", plainText);
            WxCpTpXmlMessage tpXmlMessage = WxCpTpXmlMessage
                    .fromXml(plainText);
            this.tpMessageRouter.route(tpXmlMessage, context);
            return "success";
        } catch (Exception ex) {
            log.error("request: {}", request.getQueryString());
            log.error(ex.getMessage(), ex);
            return "error";
        }
    }

    /**
     * 第三方应用安装完毕回调地址(增加session存储回调信息,防止用户刷新页面丢失配置信息)
     *
     * @return
     */
    @GetMapping("/installed")
    public void home(@RequestParam(value = "auth_code", required = false) String authCode,
                       @RequestParam(value = "expires_in", required = false) String expiresIn,
                       @RequestParam(value = "state", required = false) String state,
                       HttpServletRequest request,
                       HttpServletResponse response) throws WxErrorException, IOException {
        String outputContent = "非法请求";
        WxCpTpPermanentCodeInfo info = null;
        try {
            info = tpService.getPermanentCodeInfo(authCode);
            request.getSession().setAttribute("WxCpTpPermanentCodeInfo", info);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (info == null) {
            info = (WxCpTpPermanentCodeInfo) request.getSession().getAttribute("WxCpTpPermanentCodeInfo");
        }
        if (info != null) {
            log.info(YELLOW("{}"), info.toJson());
            outputContent = String.format("授权成功: 请保存以下内容(并提供给应用服务商)。</br> tenantId: %s </br> 永久授权码: %s </br> 授权人: %s </br> corpid: %s </br> agentid: %s </br>",
                    state,
                    info.getPermanentCode(),
                    info.getAuthUserInfo().getUserId(),
                    info.getAuthCorpInfo().getCorpId(),
                    info.getAuthInfo().getAgents().get(0).getAgentId());
            log.info(ColorOutput.BLUE(outputContent.replace("</br>", "\n")));
            final WxCpTpPermanentCodeInfo finalInfo = info;
            authBindingListeners.forEach(authBindingListener -> {
                authBindingListener.listener(state, finalInfo, request, response);
            });
        }
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(BLUE(":::: 第三方应用安装回调地址: " + properties.getCallbackUrl() + "/installed"));
        log.info(BLUE(":::: 第三方应用回调地址: " + properties.getCallbackUrl() + "/message"));
        log.info(BLUE(":::: 第三方应用可信域名配置地址: " + properties.getCallbackUrl() + "/" + jsSdkVerify.getVerifyTxtPath()));
    }
}
