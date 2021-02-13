package io.github.kilmajster.ngrok.util;

import io.github.kilmajster.ngrok.NgrokComponent;
import io.github.kilmajster.ngrok.exception.NgrokDownloadException;
import io.github.kilmajster.ngrok.os.NgrokPlatformDetector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@NgrokComponent
public class NgrokDownloader {

    private static final Logger log = LoggerFactory.getLogger(NgrokDownloader.class);

    @Value("${ngrok.binary.windows32:https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip}")
    private String windowsBinaryUrl;

    @Value("${ngrok.binary.linux32:https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip}")
    private String linuxBinaryUrl;

    @Value("${ngrok.binary.osx32:https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip}")
    private String osxBinaryUrl;

    @Value("${ngrok.binary.windows:https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-amd64.zip}")
    private String windows64BinaryUrl;

    @Value("${ngrok.binary.windows:https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip}")
    private String linux64BinaryUrl;

    @Value("${ngrok.binary.osx:https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-amd64.zip}")
    private String osx64BinaryUrl;

    @Value("${ngrok.binary.custom:}")
    private String ngrokBinaryCustom;

    @Autowired
    private final NgrokPlatformDetector platformDetector;

    public NgrokDownloader(final NgrokPlatformDetector platformDetector) {
        this.platformDetector = platformDetector;
    }

    public void downloadAndExtractNgrokTo(String destinationPath) throws NgrokDownloadException {
        String downloadedFilePath = downloadNgrokTo(destinationPath);
        NgrokFileExtractUtils.extractArchive(downloadedFilePath, destinationPath);
    }

    public String downloadNgrokTo(String destinationPath) throws NgrokDownloadException {
        String zipFileName = getFileNameFromUrl(getBinaryUrl());
        String destinationFile = FilenameUtils.concat(destinationPath, zipFileName);

        if (Files.exists(Paths.get(destinationFile))) {
            log.info("Skipping downloading, cached archive available at {}", destinationFile);

            return destinationFile;
        }

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
            log.warn("Failed to download ngrok from {}.", getBinaryUrl());
            throw new NgrokDownloadException(e);
        }
    }

    private String getFileNameFromUrl(String url) {
        return url.substring(getBinaryUrl().lastIndexOf("/") + 1);
    }

    public String getBinaryUrl() {
        if(StringUtils.isNotBlank(ngrokBinaryCustom)) {
            return ngrokBinaryCustom;
        }

        if (platformDetector.isWindows()) {
            return platformDetector.is64bitOS() ? windows64BinaryUrl : windowsBinaryUrl;
        }

        if (platformDetector.isMacOS()) {
            return platformDetector.is64bitOS() ? osx64BinaryUrl : osxBinaryUrl;
        }

        if (platformDetector.isLinux()) {
            return platformDetector.is64bitOS() ? linux64BinaryUrl : linuxBinaryUrl;
        }

        throw new NgrokDownloadException("Unsupported OS ");
    }
}