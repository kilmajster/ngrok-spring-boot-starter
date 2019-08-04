package io.github.createam.ngrok.control;

import io.github.createam.ngrok.exception.CommandExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@ConditionalOnProperty(name = "ngrok.enabled", havingValue = "true")
@Component
public class SystemCommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(SystemCommandExecutor.class);

    private final long waitForStartupMillis;

    public SystemCommandExecutor(
            @Value("${ngrok.waitForStartup.millis:3000}")long waitForStartupMillis) {
        this.waitForStartupMillis = waitForStartupMillis;
    }

    public void execute(String command) {
        try {
            Runtime.getRuntime().exec(command);
            Thread.sleep(waitForStartupMillis);
        } catch (IOException | InterruptedException e) {
            log.error("Failed to run: " + command, e);

            throw new CommandExecuteException("Error while executing: " + command, e);
        }
    }
}
