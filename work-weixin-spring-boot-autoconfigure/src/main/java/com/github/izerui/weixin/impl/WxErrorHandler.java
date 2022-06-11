package com.github.izerui.weixin.impl;

import com.github.izerui.weixin.ConfigStorageAdpatder;
import com.github.izerui.weixin.support.ColorOutput;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class WxErrorHandler implements InvocationHandler {

    private Object object;
    private ApplicationContext applicationContext;

    public WxErrorHandler(Object object, ApplicationContext applicationContext) {
        this.object = object;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            result = method.invoke(object, args);
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof WxErrorException) {
                captureWxErrorExceptionAndThrow((WxErrorException) ex.getTargetException());
            } else {
                throw ex;
            }
        }
        return result;
    }

    public void captureWxErrorExceptionAndThrow(WxErrorException e) throws WxErrorException {
        //  处理微信特定异常码
        switch (e.getError().getErrorCode()) {
            case 40084:
                getBean(ConfigStorageAdpatder.class).deleteTenantConfig();
                throw e;
            case 40085:
                getBean(WxCpTpConfigStorage.class).expireSuiteTicket();
                throw e;
            case 701003:
                log.warn(ColorOutput.YELLOW("忽略已经激活的账号"));
                return;
            default:
                throw e;
        }
    }

    private <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }
}
