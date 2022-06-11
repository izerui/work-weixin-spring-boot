package com.github.izerui.weixin22.ureport;

import com.github.izerui.weixin.CpService;
import com.github.izerui.weixin.WxProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ConfigDataSource {

    @Autowired
    private CpService cpService;


    public List<WxProperties.CpConfig> getConfigDataSource(String dataSourceName, String dataSetName, Map<String, Object> params) {
        return cpService.getConfigs();
    }

}
