package com.github.izerui;

import com.github.izerui.weixin.TpService;
import com.github.izerui.weixin.impl.mapping.ListOrderAccountsResp;
import com.github.izerui.weixin.impl.mapping.RenewUserJobReq;
import com.github.izerui.weixin.impl.mapping.RenewUserJobResp;
import com.github.izerui.weixin22.Application;
import com.google.common.collect.Lists;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpUserService;
import me.chanjar.weixin.cp.bean.*;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.bean.message.WxCpMessageSendResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 第三方应用测试
 */
@SpringBootTest(classes = Application.class)
public class TpTests {

    @Autowired
    private TpService tpService;

    @Test
    public void getSuiteAccessToken() throws WxErrorException {
        // 获取第三方应用凭证: https://developer.work.weixin.qq.com/document/path/90600
        String suiteAccessToken = tpService.getSuiteAccessToken();
        System.out.println("第三方应用凭证: " + suiteAccessToken);
    }

    @Test
    public void createInstallUrl() throws WxErrorException {
        String preAuthUrl = tpService.getPreAuthUrl("https://local-dev.yj2025.com", "feike", 1);
        System.out.println("安装第三方应用地址: " + preAuthUrl);
    }

    @Test
    public void getAuthInfo() throws WxErrorException {
        String authCorpId = "ww7c4f40dafaee2f4c";
        String permanentCode = "k6QRaIefAYf3Y_gxy5c1S-83vw8xFi-ZoXgV9MjtuxQ";
        WxCpTpAuthInfo info = tpService.getAuthInfo(authCorpId, permanentCode);
        System.out.println("企业信息: " + info.toJson());
    }

    @Test
    public void getAuthAccessToken() throws WxErrorException {
        String authCorpId = "ww7c4f40dafaee2f4c";
        String permanentCode = "k6QRaIefAYf3Y_gxy5c1S-83vw8xFi-ZoXgV9MjtuxQ";
        WxAccessToken corpToken = tpService.getCorpToken(authCorpId, permanentCode, true);
        System.out.println("企业token: " + corpToken.getAccessToken());
    }


    @Test
    public void createOrder() throws WxErrorException {
        String orderId = tpService.getLicenseService().createOrder("feike", 5, false, 1);
        System.out.println(orderId);
    }

    @Test
    public void activeAccount() throws WxErrorException {
        String activeCode = "LA100000001000000629AF7D6";
        tpService.getLicenseService().activeAccount("feike", activeCode, "serv");
    }

    @Test
    public void renewOrder() throws WxErrorException {
        RenewUserJobResp jobResp = tpService.getLicenseService().renewOrderJob("feike", Lists.newArrayList(new RenewUserJobReq("serv", 1)));
        System.out.println(jobResp);
    }

    @Test
    public void getUserId() throws WxErrorException {
        String userId = tpService.getCpService("yunji-wode").getUserService().getUserId("13911523134");
        System.out.println("查询到手机号对应的userId: " + userId);
    }

    @Test
    public void searchContect() throws WxErrorException {
        WxCpTpContactSearch search = new WxCpTpContactSearch();
        search.setAuthCorpId(tpService.getConfigOperator().getCorpId("yunji-wode"))
                .setAgentId(1000061)
                .setLimit(50)
                .setType(1)
                .setQueryWord("s");
        WxCpTpContactSearchResp searchResp = tpService.getWxCpTpContactService().contactSearch(search);
        System.out.println(searchResp.toString());
    }

    /**
     * https://developer.work.weixin.qq.com/document/path/91201
     *
     * @throws WxErrorException
     */
    @Test
    @Deprecated
    public void listDept() throws WxErrorException {
        List<WxCpDepart> departList = tpService.getCpService("yunji-wode").getDepartmentService().list(null);
        System.out.println("查询到部门列表: " + departList);
    }

    @Test
    public void getUserInfo() throws WxErrorException {
        WxCpUserService userService = tpService.getCpService("yunji-wode").getUserService();
        WxCpUser byId = userService.getById("serv");
        System.out.println(byId.toJson());
    }

    @Test
    public void testSendMsg() throws WxErrorException {
        WxCpMessage message = new WxCpMessage();
//    message.setAgentId(configStorage.getAgentId());
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        message.setToUser("serv");
        message.setContent("11111欢迎欢迎，热烈欢迎\n换行测试\n超链接:<a href=\"http://www.baidu.com\">Hello World</a>");
        WxCpMessageSendResult messageSendResult = tpService.getCpService("yunji-wode").getMessageService().send(message);
        System.out.println(messageSendResult.toString());
    }


    @Test
    public void sendFeike() throws WxErrorException {
        WxCpMessage message = new WxCpMessage();
//    message.setAgentId(configStorage.getAgentId());
        message.setMsgType(WxConsts.KefuMsgType.TEXT);
        message.setToUser("serv");
        message.setContent("11111欢迎欢迎，热烈欢迎\n换行测试\n超链接:<a href=\"http://www.baidu.com\">Hello World</a>");
        WxCpMessageSendResult messageSendResult = tpService.getCpService("feike").getMessageService().send(message);
        System.out.println(messageSendResult.toString());
    }

    @Test
    public void listOrderAccounts() throws WxErrorException {
        ListOrderAccountsResp listOrderAccounts = tpService.getLicenseService().listOrderAccounts("OI0000083A8E09629AF7D642A917ET", 100, null);
        for (ListOrderAccountsResp.OrderAccount orderAccount : listOrderAccounts.getAccountList()) {
            System.out.println(orderAccount.getActiveCode() + " 已激活: " + orderAccount.getUserId());
        }
    }

}
