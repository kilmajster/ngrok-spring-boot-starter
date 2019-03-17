package io.github.createam.ngrok;

import mockit.MockUp;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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


    @Test
    public void run_shouldLogTunnelsDetailsWhenNgrokIsRunning() throws IOException {
        // given
        when(ngrokApiClient.isResponding()).thenReturn(true);

        // when
        ngrokRunner.run();

        // then
        verify(ngrokApiClient).fetchTunnels();
    }

    @Ignore
    @Test
    public void run_whenCachedNgrokNotRunningThenShouldRunIt() throws IOException {
        // given
        when(ngrokApiClient.isResponding()).thenReturn(false);

        new MockUp<Files>() {

            @mockit.Mock
            public boolean isExecutable(Path path) {
                return true;
            }
        };

        // when
        ngrokRunner.run();

        // then
        verify(ngrokApiClient).fetchTunnels();
    }




}