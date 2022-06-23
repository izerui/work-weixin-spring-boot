package com.github.izerui.weixin.impl.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author tanwei
 * @version 1.0
 * @date 2022/06/23
 */
@Data
@AllArgsConstructor
public class TransferLicenseRep {
    /**
     * 转移成员userID
     */
    private String handoverUserId;

    /**
     * 接收成员userID
     */
    private String takeoverUserId;
}
