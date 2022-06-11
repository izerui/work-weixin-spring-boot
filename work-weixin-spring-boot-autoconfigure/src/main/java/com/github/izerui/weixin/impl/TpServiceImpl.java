package com.github.izerui.weixin.impl;

import com.github.izerui.weixin.CpService;
import com.github.izerui.weixin.TpLicenseService;
import com.github.izerui.weixin.TpService;
import com.github.izerui.weixin.WxProperties;
import com.github.izerui.weixin.config.ConfigOperator;
import me.chanjar.weixin.cp.tp.service.WxCpTpDepartmentService;
import me.chanjar.weixin.cp.tp.service.WxCpTpMediaService;
import me.chanjar.weixin.cp.tp.service.WxCpTpOAService;
import me.chanjar.weixin.cp.tp.service.WxCpTpUserService;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpServiceImpl;

public class TpServiceImpl extends WxCpTpServiceImpl implements TpService {

    private CpService cpService;
    private WxProperties properties;

    private TpLicenseService licenseService;

    public void setCpService(CpService cpService) {
        this.cpService = cpService;
    }

    public void setProperties(WxProperties properties) {
        this.properties = properties;
    }

    @Override
    public ConfigOperator getConfigOperator() {
        return cpService.getConfigOperator();
    }

    @Override
    public TpLicenseService getLicenseService() {
        if (licenseService == null) {
            licenseService = new TpLicenseServiceImpl(this, properties);
        }
        return licenseService;
    }

    @Override
    public CpService getCpService(String tenantId) {
        return cpService.tenant(tenantId, true);
    }

    @Override
    public WxProperties.TpAuthConfig getAuthConfig(String tenantId) {
        WxProperties.CpConfig cpConfig = cpService.tenant(tenantId, true).getConfig(tenantId);
        return WxProperties.TpAuthConfig.fromCpConfig(cpConfig);
    }

    @Override
    public WxCpTpUserService getWxCpTpUserService() {
        throw new UnsupportedOperationException("不支持当前操作");
    }

    @Override
    public WxCpTpDepartmentService getWxCpTpDepartmentService() {
        throw new UnsupportedOperationException("不支持当前操作");
    }

    @Override
    public WxCpTpOAService getWxCpTpOAService() {
        throw new UnsupportedOperationException("不支持当前操作");
    }

    @Override
    public WxCpTpMediaService getWxCpTpMediaService() {
        throw new UnsupportedOperationException("不支持当前操作");
    }

}
