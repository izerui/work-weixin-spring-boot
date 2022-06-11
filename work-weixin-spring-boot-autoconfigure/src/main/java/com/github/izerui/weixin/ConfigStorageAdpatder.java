package com.github.izerui.weixin;

import com.github.izerui.weixin.config.ConfigOperator;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;

public interface ConfigStorageAdpatder extends WxCpConfigStorage {

    /**
     * 切换租户，并且声明是否是第三方应用
     *
     * @param tenantId
     * @param isThirdApp
     * @return
     */
    ConfigStorageAdpatder tenant(String tenantId, boolean isThirdApp);

    /**
     * 当前请求是否是第三方应用
     *
     * @return
     */
    boolean isThirdApp();

    /**
     * 当前请求指定的租户
     *
     * @return
     */
    String tenantId();

    /**
     * 支持租户的配置操作对象
     *
     * @return
     */
    ConfigOperator getConfigOperator();

    /**
     * 永久授权码
     *
     * @return
     */
    String getPermanentCode();

    /**
     * 删除当前tenantId对应的配置
     */
    void deleteTenantConfig();
}
