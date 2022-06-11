package com.github.izerui.weixin.impl.mapping;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;

import java.util.List;

@Data
@AllArgsConstructor
public class RenewUserJobResp extends WxCpBaseResp {

    @SerializedName("jobid")
    private String jobId;

    @SerializedName("invalid_account_list")
    private List<AccountJob> accountList;

    @NoArgsConstructor
    @Data
    public static class AccountJob {
        private Integer errcode;

        @SerializedName("errmsg")
        private String errmsg;

        @SerializedName("userid")
        private String userid;

        private Integer type;
    }

    public static RenewUserJobResp fromJson(String json) {
        return WxCpGsonBuilder.create().fromJson(json, RenewUserJobResp.class);
    }
}
