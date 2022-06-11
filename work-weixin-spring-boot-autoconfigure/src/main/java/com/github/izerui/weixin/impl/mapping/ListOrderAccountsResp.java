package com.github.izerui.weixin.impl.mapping;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class ListOrderAccountsResp extends WxCpBaseResp {

  private static final long serialVersionUID = -3115552079069452091L;
  @SerializedName("next_cursor")
  private String nextCursor;

  @SerializedName("has_more")
  private Integer hasMore;

  @SerializedName("account_list")
  private List<OrderAccount> accountList;

  @NoArgsConstructor
  @Data
  public static class OrderAccount {
    @SerializedName("active_code")
    private String activeCode;
    @SerializedName("userid")
    private String userId;
    private Integer type;
  }

  public static ListOrderAccountsResp fromJson(String json) {
    return WxCpGsonBuilder.create().fromJson(json, ListOrderAccountsResp.class);
  }
}
