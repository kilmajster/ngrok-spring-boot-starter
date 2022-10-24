package ngrok.download;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokComponent;
import ngrok.configuration.NgrokConfiguration;
import ngrok.exception.NgrokDownloadException;
import ngrok.os.NgrokPlatformDetector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@NgrokComponent
@RequiredArgsConstructor
public class NgrokDownloader {

    private final NgrokArchiveUrlProvider ngrokArchiveUrlProvider;
    private final NgrokConfiguration ngrokConfiguration;
    private final NgrokPlatformDetector platformDetector;

    public void downloadAndExtractNgrokTo(String destinationPath) throws NgrokDownloadException {
        String downloadedFilePath = downloadNgrokTo(destinationPath);
        NgrokFileExtractUtils.extractArchive(downloadedFilePath, destinationPath);
    }

    private String downloadNgrokTo(String destinationPath) throws NgrokDownloadException {
        String archiveFileName = getFileNameFromUrl(getBinaryUrl());
        String destinationFile = FilenameUtils.concat(destinationPath, archiveFileName);

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

    private String getBinaryUrl() {
        if (StringUtils.isNotBlank(ngrokConfiguration.getCustomArchiveUrl())) {
            return ngrokConfiguration.getCustomArchiveUrl();
        }

        if (platformDetector.isWindows()) {
            return platformDetector.is64bitOS() ? ngrokArchiveUrlProvider.getWindows() : ngrokArchiveUrlProvider.getWindows32();
        }

        if (platformDetector.isMacOS()) {
            return platformDetector.is64bitOS() ? ngrokArchiveUrlProvider.getOsx() : ngrokArchiveUrlProvider.getOsx32();
        }

        if (platformDetector.isLinux()) {
            return platformDetector.is64bitOS() ? ngrokArchiveUrlProvider.getLinux() : ngrokArchiveUrlProvider.getLinux32();
        }

        throw new NgrokDownloadException("Unsupported OS");
    }
}
