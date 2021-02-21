package ngrok.runner;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext
@RunWith(SpringRunner.class)
public abstract class BaseNgrokRunnerIntegrationTest {

    @Before
    public void waitForAppReadyEventAndNgrokStartup() throws InterruptedException {
        Thread.sleep(1000);
    }
}