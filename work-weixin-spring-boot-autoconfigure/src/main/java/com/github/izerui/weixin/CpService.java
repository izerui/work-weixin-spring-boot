package com.github.izerui.weixin;

import com.github.izerui.weixin.config.ConfigOperator;
import me.chanjar.weixin.cp.api.WxCpService;

import java.util.List;

/**
 * 应用平台servcie
 * @author liuyuhua
 * @date 2022/4/18
 */
public interface CpService extends WxCpService {

    /**
     * 指定以某一个租户操作service请求
     *
     * @param tenantId   租户ID
     * @param isThirdApp 是否是第三方应用
     * @return
     */
    CpService tenant(String tenantId, boolean isThirdApp);

    /**
     * 根据租户获取配置
     *
     * @param tenantId
     * @return
     */
    WxProperties.CpConfig getConfig(String tenantId);

    /**
     * 获取配置的租户信息列表
     *
     * @return
     */
    List<WxProperties.CpConfig> getConfigs();

    /**
     * 获取配置操作对象
     *
     * @return
     */
    ConfigOperator getConfigOperator();

    /**
     * 获取桥接的存储适配器
     *
     * @return
     */
    ConfigStorageAdpatder getStorageAdpatder();

    /**
     * 获取第三方应用service
     *
     * @return
     */
    TpService getTpService();
}
