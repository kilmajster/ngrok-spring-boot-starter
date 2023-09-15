package ngrok.configuration;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;


public class NgrokAuthTokenUtil {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static Optional<String> getAuthToken(final String ngrokYamlConfigPath, final String ngrokDirectory) {
        final String defaultNgrokConfigFilePath = ngrokDirectory + File.separator + NgrokConfiguration.NGROK_CONFIG_FILE_NAME;
        boolean defaultConfigFileExist = Files.exists(Paths.get(defaultNgrokConfigFilePath));
        if (defaultConfigFileExist) {
            String authToken = readNgrokConfig(defaultNgrokConfigFilePath).getAuthToken();
            if (Objects.nonNull(authToken)) {
                return Optional.of(authToken);
            }
        }

        try {
            return Stream.of(StringUtils.split(ngrokYamlConfigPath, NgrokConfiguration.NGROK_CONFIG_FILES_SEPARATOR))
                    .map(path -> readNgrokConfig(path).getAuthToken())
                    .findAny();
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @SneakyThrows
    private static NgrokYamlConfigModel readNgrokConfig(String path) {
        return mapper.readValue(new File(path), NgrokYamlConfigModel.class);
    }

    @Data
    @NoArgsConstructor
    private static class NgrokYamlConfigModel {
        @JsonAlias("authtoken")
        private String authToken;
    }
}
