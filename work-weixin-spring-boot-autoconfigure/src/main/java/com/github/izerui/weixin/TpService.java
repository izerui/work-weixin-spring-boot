package com.github.izerui.weixin;

import com.github.izerui.weixin.config.ConfigOperator;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import me.chanjar.weixin.cp.tp.service.*;

/**
 * 第三方服务平台service
 */
public interface TpService extends WxCpTpService {

    /**
     * 获取企业微信配置存储对象
     *
     * @return
     */
    WxCpTpConfigStorage getWxCpTpConfigStorage();

    /**
     * 对应的配置操作对象
     *
     * @return
     */
    ConfigOperator getConfigOperator();

    /**
     * @return
     */
    TpLicenseService getLicenseService();


    /**
     * 获取指定租户的调用对象
     *
     * @param tenantId
     * @return
     */
    CpService getCpService(String tenantId);

    /**
     * 根据tenantId获取授权企业的配置
     *
     * @param tenantId
     * @return
     */
    WxProperties.TpAuthConfig getAuthConfig(String tenantId);

    /**
     * 请使用 {@link #getCpService(String)}} 获取其相应的service操作对象
     *
     * @return
     */
    @Deprecated
    @Override
    WxCpTpUserService getWxCpTpUserService();

    /**
     * 请使用 {@link #getCpService(String)}} 获取其相应的service操作对象
     *
     * @return
     */
    @Deprecated
    @Override
    WxCpTpDepartmentService getWxCpTpDepartmentService();

    /**
     * 请使用 {@link #getCpService(String)}} 获取其相应的service操作对象
     *
     * @return
     */
    @Deprecated
    @Override
    WxCpTpOAService getWxCpTpOAService();

    /**
     * 请使用 {@link #getCpService(String)}} 获取其相应的service操作对象
     *
     * @return
     */
    @Deprecated
    @Override
    WxCpTpMediaService getWxCpTpMediaService();

    /**
     * 暴露 {@link #post(String, String, boolean)}
     *
     * @param url
     * @param postData
     * @param withoutSuiteAccessToken
     * @return
     * @throws WxErrorException
     */
    String post(String url, String postData, boolean withoutSuiteAccessToken) throws WxErrorException;

}
