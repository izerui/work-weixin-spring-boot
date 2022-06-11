package com.github.izerui.weixin22.ureport;

import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.github.izerui.weixin.WxProperties;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static com.github.izerui.weixin.support.ColorOutput.BLUE;


@Component
@Slf4j
public class PathReportProvider implements ReportProvider , CommandLineRunner {

    @Autowired
    private WxProperties properties;

    @Override
    public InputStream loadReport(String name) {
        try {
            Resource resource = new PathMatchingResourcePatternResolver().getResource(name);
            return resource.getInputStream();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteReport(String name) {
        throw new UnsupportedOperationException("不支持");
    }

    @Override
    public List<ReportFile> getReportFiles() {
        return Lists.newArrayList(new ReportFile("classpath:/template/template.ureport.xml", new Date()));
    }

    @Override
    public void saveReport(String name, String content) {
        throw new UnsupportedOperationException("不支持");
    }

    @Override
    public String getName() {
        return "classpath";
    }

    @Override
    public boolean disabled() {
        return false;
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(BLUE(":::: 查看缓存的配置: " + properties.getCallbackUrl() + "/ureport/preview?_u=classpath:template/template.ureport.xml&_t=1,6,9"));
    }
}
