package ngrok.os;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ngrok.NgrokComponent;
import ngrok.configuration.NgrokConfiguration;
import ngrok.exception.NgrokCommandExecuteException;

import java.io.IOException;

@Slf4j
@NgrokComponent
@RequiredArgsConstructor
public class NgrokSystemCommandExecutor {

    private final NgrokConfiguration ngrokConfiguration;

    public void execute(final String command) {
        try {
            Runtime.getRuntime().exec(command);
            Thread.sleep(ngrokConfiguration.getStartupDelay());
        } catch (IOException | InterruptedException e) {
            log.error("Failed to run: " + command, e);

            throw new NgrokCommandExecuteException("Error while executing: " + command, e);
        }
    }
}
