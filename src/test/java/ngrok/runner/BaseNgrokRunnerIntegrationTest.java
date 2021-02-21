package ngrok.runner;


import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
public abstract class BaseNgrokRunnerIntegrationTest {

    private final Logger log = LoggerFactory.getLogger(BaseNgrokRunnerIntegrationTest.class);

    @BeforeEach
    public void waitForAppReadyEventAndNgrokStartup() throws InterruptedException {
        log.debug("Waiting 1s for ApplicationReadyEvent and ngrok starter logic to be done...");
        Thread.sleep(1000);
    }
}