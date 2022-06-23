package com.github.izerui.weixin.impl.mapping;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;

import java.util.List;

/**
 * @author tanwei
 * @version 1.0
 * @date 2022/06/23
 */
@Data
@AllArgsConstructor
public class TransferLicenseResp extends WxCpBaseResp {

    @SerializedName("transfer_result")
    private List<transfer> transferResult;

    @NoArgsConstructor
    @Data
    public static class transfer{
        @SerializedName("handover_userid")
        private String handoverUserId;

        @SerializedName("takeover_userid")
        private String takeoverUserId;

        @SerializedName("errcode")
        private String errCode;
    }

    public static TransferLicenseResp fromJson(String json) {
        return WxCpGsonBuilder.create().fromJson(json, TransferLicenseResp.class);
    }
}
