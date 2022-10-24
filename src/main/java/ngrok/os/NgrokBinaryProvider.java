package ngrok.os;

import lombok.RequiredArgsConstructor;
import ngrok.NgrokComponent;
import ngrok.configuration.NgrokConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@NgrokComponent
@RequiredArgsConstructor
public class NgrokBinaryProvider {

    private final NgrokConfiguration ngrokConfiguration;
    private final NgrokSystemCommandExecutor ngrokSystemCommandExecutor;

    public String getNgrokBinaryFilePath() {
        String executable = SystemUtils.IS_OS_WINDOWS ? "ngrok.exe" : "ngrok";

        return (ngrokSystemCommandExecutor.isPresentInPath(executable) && ngrokConfiguration.isUseFromPath())
                ? executable
                : getNgrokDirectoryOrDefault() + File.separator + executable;
    }

    public String getNgrokDirectoryOrDefault() {
        return StringUtils.isNotBlank(ngrokConfiguration.getDirectory()) ?
                ngrokConfiguration.getDirectory() : getDefaultNgrokDirectory();
    }

    public boolean isNgrokBinaryPresent() {
        return Files.isExecutable(Paths.get(getNgrokBinaryFilePath()));
    }

    private String getDefaultNgrokDirectory() {
        return FilenameUtils.concat(
                FileUtils.getUserDirectory().getPath(),
                ngrokConfiguration.isLegacy()
                        ? NgrokConfiguration.NGROK_LEGACY_DIRECTORY_NAME
                        : NgrokConfiguration.NGROK_DIRECTORY_NAME
        );
    }
}
