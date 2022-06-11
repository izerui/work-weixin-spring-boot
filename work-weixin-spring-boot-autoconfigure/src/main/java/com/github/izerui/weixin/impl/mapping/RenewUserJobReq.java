package com.github.izerui.weixin.impl.mapping;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RenewUserJobReq {

    @SerializedName("userid")
    private String userId;

    private Integer type;
}
