package ngrok.configuration;

import lombok.RequiredArgsConstructor;
import ngrok.NgrokComponent;
import ngrok.exception.NgrokMalformedConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

@NgrokComponent
@RequiredArgsConstructor
public class NgrokConfigurationProvider {

    private static final String CLASSPATH_CONFIG_PREFIX = "classpath:";

    private final NgrokConfiguration ngrokConfiguration;

    public String prepareNgrokConfigParams() {
        return isConfigPresent() ? loadNgrokConfigFile() : "";
    }

    private String loadNgrokConfigFile() {
        return isClasspathConfig() ? handleClasspathConfig() : loadConfigurationFile(ngrokConfiguration.getConfigPath());
    }

    private String handleClasspathConfig() {
        try {
            String tempConfigPath = copyClassPathConfigToTempFile();

            return loadConfigurationFile(tempConfigPath);
        } catch (Exception e) {
            throw new NgrokMalformedConfigurationException("Error while loading ngrok classpath configuration!", e);
        }
    }

    private String copyClassPathConfigToTempFile() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(getClassPathConfigFilePath());
        String configContent = FileUtils.readFileToString(classPathResource.getFile(), Charset.defaultCharset());

        File configTempFile = Files.createTempFile(
                extractFileName(classPathResource.getFilename()),
                extractFileExtension(classPathResource.getFilename())
        ).toFile();

        FileUtils.writeStringToFile(configTempFile, configContent, Charset.defaultCharset());

        return configTempFile.getAbsolutePath();
    }

    private String getClassPathConfigFilePath() {
        return StringUtils.removeStart(ngrokConfiguration.getConfigPath(), CLASSPATH_CONFIG_PREFIX);
    }

    private String extractFileName(final String fullFilename) {
        return StringUtils.split(fullFilename, ".")[0];
    }

    private String extractFileExtension(final String fullFilename) {
        return ".".concat(StringUtils.split(fullFilename, ".")[1]);
    }

    private String loadConfigurationFile(final String configPath) {
        return StringUtils.split(configPath, ";").length == 1 ? "-config " + configPath + " " // 1 config arg
                : "-config " + String.join(" -config ", StringUtils.split(configPath, ";")) + " "; // multiple configs
    }

    private boolean isConfigPresent() {
        return StringUtils.isNotBlank(ngrokConfiguration.getConfigPath());
    }

    private boolean isClasspathConfig() {
        return StringUtils.startsWith(ngrokConfiguration.getConfigPath(), CLASSPATH_CONFIG_PREFIX);
    }
}
