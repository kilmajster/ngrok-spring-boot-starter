package io.github.kilmajster.ngrok.control;

import io.github.kilmajster.ngrok.exception.NgrokCommandExecuteException;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@ConditionalOnProperty(name = "ngrok.enabled", havingValue = "true")
@Component
public class NgrokSystemCommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(NgrokSystemCommandExecutor.class);

    private final long waitForStartupMillis;

    public NgrokSystemCommandExecutor(
            @Value("${ngrok.waitForStartup.millis:3000}")long waitForStartupMillis) {
        this.waitForStartupMillis = waitForStartupMillis;
    }

    public void execute(String command) {
        try {
            Runtime.getRuntime().exec(command);
            Thread.sleep(waitForStartupMillis);
        } catch (IOException | InterruptedException e) {
            log.error("Failed to run: " + command, e);

            throw new NgrokCommandExecuteException("Error while executing: " + command, e);
        }
    }
}
