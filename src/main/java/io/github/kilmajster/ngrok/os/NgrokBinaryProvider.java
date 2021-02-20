package io.github.kilmajster.ngrok.os;

import io.github.kilmajster.ngrok.NgrokComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@NgrokComponent
public class NgrokBinaryProvider {

    @Value("${ngrok.directory:}")
    private String ngrokDirectory;

    public String getNgrokBinaryFilePath() {
        String executable = SystemUtils.IS_OS_WINDOWS ? "ngrok.exe" : "ngrok";

        return getNgrokDirectoryOrDefault() + File.separator + executable;
    }

    public String getNgrokDirectoryOrDefault() {
        return StringUtils.isNotBlank(ngrokDirectory) ? ngrokDirectory : getDefaultNgrokDirectory();
    }

    public boolean isNgrokBinaryPresent() {
        return Files.isExecutable(Paths.get(getNgrokBinaryFilePath()));
    }

    private String getDefaultNgrokDirectory() {
        return FilenameUtils.concat(FileUtils.getUserDirectory().getPath(), ".ngrok2");
    }
}