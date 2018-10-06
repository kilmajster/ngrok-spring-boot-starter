package com.createam.ngrok.experimental.embedded;

import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:linux.properties")
public class NgrokLinuxConfiguration implements NgrokConfiguration {

    private final String cachePatch;

    @URL
    @Value("${ngrok.binary.url}")
    private String binaryUrl;

    @Value("${ngrok.binary.name}")
    private String binaryName;

    public NgrokLinuxConfiguration(String cachePatch) {
        this.cachePatch = cachePatch;
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
        return cachePatch;
    }
}
