package com.createam.ngrok.experimental.embedded;

import org.apache.commons.io.FileUtils;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import java.io.File;

@PropertySource("classpath:windows.properties")
public class NgrokWindowsConfiguration implements NgrokConfiguration {

    private final String cachePath;

    @URL
    @Value("${ngrok.binary.url}")
    private String binaryUrl;

    @Value("${ngrok.binary.name}")
    private String binaryName;

    public NgrokWindowsConfiguration(final String cachePath) {
        this.cachePath = cachePath;
    }

    @Override
    public String getBinaryUrl() {
        return binaryUrl;
    }

    @Override
    public String getBinaryName() {
        return binaryName;
    }

    @Override
    public String getCachePatch() {
        return StringUtils.isEmpty(cachePath)
                ? FileUtils.getUserDirectory().getPath().concat(File.separator).concat(".ngrok2")
                : cachePath;
    }
}
