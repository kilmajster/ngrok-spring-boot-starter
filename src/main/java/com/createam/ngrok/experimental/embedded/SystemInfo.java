package com.createam.ngrok.experimental.embedded;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class SystemInfo {

    private static final Logger log = LoggerFactory.getLogger(SystemInfo.class);

    public static SystemType checkOS() {
        String osName = System.getProperty("os.name");

        if(StringUtils.startsWithIgnoreCase(osName, "windows")) {
            log.info("Detected Windows as OS");
            return SystemType.WINDOWS;
        }

        if(StringUtils.startsWithIgnoreCase(osName, "linux")) {
            log.info("Detected Linux as OS");
            return SystemType.LINUX;
        }

        return SystemType.UNDEFINED;
    }

}
