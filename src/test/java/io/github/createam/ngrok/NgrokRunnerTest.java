package io.github.createam.ngrok;

import io.github.createam.ngrok.data.Tunnel;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class NgrokRunnerTest {

    @Mock
    private NgrokApiClient ngrokApiClient;

    @Mock
    private NgrokDownloader ngrokDownloader;

    @InjectMocks
    private NgrokRunner ngrokRunner;

    @Rule
    public final OutputCapture logsCapture = new OutputCapture();

    private TaskExecutor testTaskExecutor = new SyncTaskExecutor();

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(ngrokRunner, "ngrokAsyncExecutor", testTaskExecutor);
    }

    @Test
    public void run_shouldLogTunnelsDetailsWhenNgrokIsRunning() {
        // given
        when(ngrokApiClient.isResponding()).thenReturn(true);

        Tunnel tunnel = new Tunnel();
        tunnel.setProto("http");
        tunnel.setPublicUrl("http://for-sure-not-exist.local");

        when(ngrokApiClient.fetchTunnels()).thenReturn(Collections.singletonList(tunnel));

        // when
        ngrokRunner.run();

        // then
        logsCapture.expect(CoreMatchers.containsString("Remote url (http) -> http://for-sure-not-exist.local"));

        verify(ngrokApiClient).isResponding();
        verify(ngrokApiClient).fetchTunnels();
    }

    @Test
    public void run_whenCachedNgrokNotRunningThenShouldRunIt() {
        // given
        when(ngrokApiClient.isResponding()).thenReturn(false);
        
        // when
        ngrokRunner.run();

        // then
        verify(ngrokApiClient).fetchTunnels();
    }


}