package io.github.kilmajster.ngrok.os;

import io.github.kilmajster.ngrok.NgrokComponent;
import org.apache.commons.lang3.SystemUtils;

@NgrokComponent
public class NgrokPlatformDetector {

    public boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    public boolean isLinux() {
        return SystemUtils.IS_OS_LINUX;
    }

    public boolean isMacOS() {
        return SystemUtils.IS_OS_MAC;
    }

    public boolean is64bitOS() {
        return SystemUtils.OS_ARCH.contains("64");
    }

    public boolean isUnix() {
        return SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC;
    }
}