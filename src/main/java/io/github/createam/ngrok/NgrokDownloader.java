package io.github.createam.ngrok;

import io.github.createam.ngrok.exception.NgrokDownloadException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@ConditionalOnProperty(name = "ngrok.enabled", havingValue = "true")
@Component
public class NgrokDownloader {

    private static final Logger log = LoggerFactory.getLogger(NgrokDownloader.class);

    @Value("${ngrok.binary.windows:https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip}")
    private String windowsBinaryUrl;

    @Value("${ngrok.binary.osx:https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip}")
    private String osxBinaryUrl;

    @Value("${ngrok.binary.linux:https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip}")
    private String linuxBinaryUrl;

    /**
     * @param destinationPath - destination path as String
     * @return downloaded file path as String
     * @throws NgrokDownloadException
     */
    public String downloadNgrokTo(String destinationPath) throws NgrokDownloadException {
        String zipFileName = getBinaryUrl().substring(getBinaryUrl().lastIndexOf("/") + 1);
        String destinationFile = destinationPath.endsWith(File.separator)
                ? destinationPath.concat(zipFileName)
                : destinationPath.concat(File.separator).concat(zipFileName);

        log.info("Downloading ngrok from {} to {}", getBinaryUrl(), destinationFile);

        File targetFile = new File(destinationFile);
        long downloadStartTime = System.currentTimeMillis();

        try {
            FileUtils.copyURLToFile(new URL(getBinaryUrl()), targetFile);

            long downloadFinishTime = System.currentTimeMillis();

            log.info("Downloaded {} kb in {} ms. It will be cached in {} for the next usage.",
                    FileUtils.sizeOf(targetFile) / 1024,
                    downloadFinishTime - downloadStartTime,
                    destinationPath);

            return destinationFile;
        } catch (IOException e) {
            log.warn("Failed to download ngrok from default mirror. You can configure it by overriding property TODO", getBinaryUrl(), destinationFile);
            throw new NgrokDownloadException(e);
        }
    }

    private String getBinaryUrl() {
        if(SystemUtils.IS_OS_WINDOWS) {
            return windowsBinaryUrl;
        }

        if(SystemUtils.IS_OS_MAC) {
            return osxBinaryUrl;
        }

        if(SystemUtils.IS_OS_LINUX) {
            return linuxBinaryUrl;
        }

        throw new RuntimeException("Unsupported OS.");
    }
}
