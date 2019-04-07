package io.github.createam.ngrok;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.createam.ngrok.data.Tunnel;
import org.apache.logging.log4j.message.Message;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class NgrokRunnerTest {

    @Mock
    private NgrokHealthChecker ngrokHealthChecker;

    @Mock
    private NgrokDownloader ngrokDownloader;

    @InjectMocks
    private NgrokRunner ngrokRunner;

    @Rule
    public final OutputCapture outputCapture = new OutputCapture();

    private TaskExecutor testTaskExecutor = new SyncTaskExecutor();

    @Before
    public void clearLoggingStatements() {
        TestLogsAppender.clearEvents();
    }

    @Test
    public void run_shouldLogTunnelsDetailsWhenNgrokIsRunning() {
        // given
        when(ngrokHealthChecker.isResponding()).thenReturn(true);

        Tunnel tunnel = new Tunnel();
        tunnel.setProto("http");
        tunnel.setPublicUrl("http://for-sure-not-exist.local");

        when(ngrokHealthChecker.fetchTunnels()).thenReturn(Collections.singletonList(tunnel));

        ReflectionTestUtils.setField(ngrokRunner, "ngrokAsyncExecutor", testTaskExecutor);

        // when
        ngrokRunner.run();

        // then


        assertThat(TestLogsAppender.getEvents())
                .extracting("formattedMessage")
                .contains("Remote url (http) -> http://for-sure-not-exist.local");

        verify(ngrokHealthChecker).isResponding();
        verify(ngrokHealthChecker).fetchTunnels();
    }

    @Ignore
    @Test
    public void run_whenCachedNgrokNotRunningThenShouldRunIt() throws IOException {
        // given
        when(ngrokHealthChecker.isResponding()).thenReturn(false);




//        new MockUp<Files>() {
//
//            @mockit.Mock
//            public boolean isExecutable(Path path) {
//                return true;
//            }
//        };

        // when
        ngrokRunner.run();

        // then
        verify(ngrokHealthChecker).fetchTunnels();
    }


}