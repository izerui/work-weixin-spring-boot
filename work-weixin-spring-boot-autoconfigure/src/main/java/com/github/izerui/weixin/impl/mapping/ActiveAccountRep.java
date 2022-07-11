package com.github.izerui.weixin.impl.mapping;

import lombok.Data;

/**
 * @author tanwei
 * @version 1.0
 * @date 2022/07/11
 */
@Data
public class ActiveAccountRep {
    /**
     * 激活码
     */
    private String activeCode;

    /**
     * 企业成员userid
     */
    private String authUserId;
}
