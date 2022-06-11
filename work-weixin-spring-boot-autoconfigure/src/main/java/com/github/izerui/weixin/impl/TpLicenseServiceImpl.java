package com.github.izerui.weixin.impl;

import com.github.izerui.weixin.TpLicenseService;
import com.github.izerui.weixin.TpService;
import com.github.izerui.weixin.WxProperties;
import com.github.izerui.weixin.impl.mapping.ListOrderAccountsResp;
import com.github.izerui.weixin.impl.mapping.RenewUserJobReq;
import com.github.izerui.weixin.impl.mapping.RenewUserJobResp;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;

import java.util.List;

public class TpLicenseServiceImpl implements TpLicenseService {

    private TpService tpService;
    private WxProperties properties;

    public TpLicenseServiceImpl(TpService tpService, WxProperties properties) {
        this.tpService = tpService;
        this.properties = properties;
    }

    @Override
    public TpService getTpService() {
        return tpService;
    }

    @Override
    public String createOrder(String tenantId, Integer accountNum, boolean external, Integer months) throws WxErrorException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("corpid", tpService.getConfigOperator().getCorpId(tenantId));
        jsonObject.addProperty("buyer_userid", properties.getTpConfig().getBuyUserId());
        JsonObject accountCountObj = new JsonObject();
        if (external) {
            accountCountObj.addProperty("external_contact_count", accountNum);
        } else {
            accountCountObj.addProperty("base_count", accountNum);
        }
        jsonObject.add("account_count", accountCountObj);

        JsonObject monthObj = new JsonObject();
        monthObj.addProperty("months", months);
        jsonObject.add("account_duration", monthObj);

        String access_token = tpService.getWxCpProviderToken();
        String respJson = tpService.post(tpService.getWxCpTpConfigStorage().getApiUrl("/cgi-bin/license/create_new_order") + "?provider_access_token=" + access_token, jsonObject.toString(), true);
        return WxGsonBuilder.create().fromJson(respJson, JsonObject.class).get("order_id").getAsString();

    }

    @Override
    public RenewUserJobResp renewOrderJob(String tenantId, List<RenewUserJobReq> authUserIds) throws WxErrorException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("corpid", tpService.getConfigOperator().getCorpId(tenantId));
        JsonArray jsonArray = new JsonArray();
        authUserIds.stream().map(s -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("userid", s.getUserId());
            obj.addProperty("type", s.getType());
            return obj;
        }).forEach(jsonArray::add);
        jsonObject.add("account_list", jsonArray);
        String access_token = tpService.getWxCpProviderToken();
        String respJson = tpService.post(tpService.getWxCpTpConfigStorage().getApiUrl("/cgi-bin/license/create_renew_order_job") + "?provider_access_token=" + access_token, jsonObject.toString(), true);
        return RenewUserJobResp.fromJson(respJson);
    }

    @Override
    public String submitOrderJob(String jobId, Integer months) throws WxErrorException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("jobid", jobId);
        jsonObject.addProperty("buyer_userid", properties.getTpConfig().getBuyUserId());

        JsonObject monthObj = new JsonObject();
        monthObj.addProperty("months", months);
        jsonObject.add("account_duration", monthObj);
        String access_token = tpService.getWxCpProviderToken();
        String respJson = tpService.post(tpService.getWxCpTpConfigStorage().getApiUrl("/cgi-bin/license/submit_order_job") + "?provider_access_token=" + access_token, jsonObject.toString(), true);
        return WxCpGsonBuilder.create().fromJson(respJson, JsonObject.class).get("order_id").getAsString();
    }

    /**
     * 激活账号
     *
     * @param tenantId
     * @param activeCode
     * @param authUserId
     * @return
     * @throws WxErrorException
     */
    @Override
    public WxCpBaseResp activeAccount(String tenantId, String activeCode, String authUserId) throws WxErrorException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("active_code", activeCode);
        jsonObject.addProperty("corpid", tpService.getConfigOperator().getCorpId(tenantId));
        jsonObject.addProperty("userid", authUserId);
        String access_token = tpService.getWxCpProviderToken();
        String post = tpService.post(tpService.getWxCpTpConfigStorage().getApiUrl("/cgi-bin/license/active_account") + "?provider_access_token=" + access_token, jsonObject.toString(), true);
        return WxCpBaseResp.fromJson(post);
    }

    @Override
    public ListOrderAccountsResp listOrderAccounts(String orderId, Integer limit, String cursor) throws WxErrorException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_id", orderId);
        jsonObject.addProperty("limit", limit);
        jsonObject.addProperty("cursor", cursor);
        String access_token = tpService.getWxCpProviderToken();
        String respJson = tpService.post(tpService.getWxCpTpConfigStorage().getApiUrl("/cgi-bin/license/list_order_account") + "?provider_access_token=" + access_token, jsonObject.toString(), true);
        return ListOrderAccountsResp.fromJson(respJson);
    }
}
