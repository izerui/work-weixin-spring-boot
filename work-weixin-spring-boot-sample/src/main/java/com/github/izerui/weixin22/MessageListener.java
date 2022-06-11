package com.github.izerui.weixin22;

import com.github.izerui.weixin.CpService;
import com.github.izerui.weixin.TpService;
import com.github.izerui.weixin.WxProperties;
import com.github.izerui.weixin.provider.CpListener;
import com.github.izerui.weixin.provider.TpListener;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.message.WxCpTpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.github.izerui.weixin.support.ColorOutput.BLUE;
import static com.github.izerui.weixin.support.ColorOutput.MAGENTA;


@Slf4j
@Component
public class MessageListener implements CpListener, TpListener {

    @Autowired
    private WxProperties properties;

    private Gson gson = new Gson().newBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public void listener(String tenantId, WxCpXmlMessage wxMessage, CpService wxCpService) {
        log.info("tenatnId: {} wxMessage: \n{}", BLUE(tenantId), MAGENTA(gson.toJson(wxMessage)));
    }

    @Override
    public void listener(WxCpTpXmlMessage wxMessage, TpService tpService) {
        log.info("wxMessage: \n{}", MAGENTA(gson.toJson(wxMessage)));

        if (wxMessage.getInfoType() != null) {
            switch (wxMessage.getInfoType()) {
                case "suite_ticket":
                    // https://developer.work.weixin.qq.com/document/path/90628
                    // https://developer.work.weixin.qq.com/document/path/90600
                    tpService.setSuiteTicket(wxMessage.getSuiteTicket(), properties.getTpConfig().getSuiteTicketExpiresTime());
                    break;
            }
        }

    }
}
