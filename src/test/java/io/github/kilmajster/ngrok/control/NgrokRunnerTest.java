package io.github.kilmajster.ngrok.control;

import io.github.kilmajster.ngrok.data.NgrokTunnel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Ignore
@RunWith(MockitoJUnitRunner.class)
public class NgrokRunnerTest {

    @Mock
    private NgrokApiClient ngrokApiClient;

    @Mock
    private NgrokDownloader ngrokDownloader;

    @Mock
    private NgrokSystemCommandExecutor systemCommandExecutor;

    @InjectMocks
    private NgrokRunner ngrokRunner;

    @Rule
    public OutputCapture logsCapture = new OutputCapture();

    private TaskExecutor testTaskExecutor = new SyncTaskExecutor();

    @Before
    public void setUp() {
        logsCapture.reset();

        ReflectionTestUtils.setField(ngrokRunner, "ngrokExecutor", testTaskExecutor);
    }

    @Test
    public void run_shouldLogTunnelsDetailsWhenNgrokIsRunning() {
        // given
        when(ngrokApiClient.isResponding()).thenReturn(true);

        NgrokTunnel tunnel = new NgrokTunnel();
        tunnel.setProto("http");
        tunnel.setPublicUrl("http://for-sure-not-exist.local");

        when(ngrokApiClient.fetchTunnels()).thenReturn(Collections.singletonList(tunnel));

        // when
        ngrokRunner.run();

        // then
        logsCapture.expect(containsString("Remote url (http) -> http://for-sure-not-exist.local"));

        verify(ngrokApiClient).isResponding();
        verify(ngrokApiClient).fetchTunnels();
    }

    @Test
    public void run_whenCachedNgrokNotRunningThenShouldRunIt() {
        // given
        when(ngrokApiClient.isResponding()).thenReturn(false);

        ReflectionTestUtils.setField(ngrokRunner, "ngrokDirectory", "not-existing-directory");
        ReflectionTestUtils.setField(ngrokRunner, "port", "8080");

        // when
        ngrokRunner.run();

        // then
        verify(ngrokApiClient).fetchTunnels();

        logsCapture.expect(containsString("Starting ngrok..."));

        verify(systemCommandExecutor).execute("not-existing-directory\\ngrok.exe http 8080");

        logsCapture.expect(containsString("Ngrok is running."));
    }
}