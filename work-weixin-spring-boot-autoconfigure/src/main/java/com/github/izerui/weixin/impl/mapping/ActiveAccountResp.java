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
 * @date 2022/07/11
 */
@Data
@AllArgsConstructor
public class ActiveAccountResp extends WxCpBaseResp {

    /**
     * 激活结果列表
     */
    @SerializedName("active_result")
    private List<active> activeResult;

    @NoArgsConstructor
    @Data
    public static class active{
        /**
         * 激活码
         */
        @SerializedName("active_code")
        private String activeCode;

        /**
         * 企业成员id
         */
        @SerializedName("userid")
        private String userId;

        /**
         * 用户激活错误码，0为成功
         */
        @SerializedName("errcode")
        private String errCode;
    }

    public static ActiveAccountResp fromJson(String json) {
        return WxCpGsonBuilder.create().fromJson(json, ActiveAccountResp.class);
    }
}
