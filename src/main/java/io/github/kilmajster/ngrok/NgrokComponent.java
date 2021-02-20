package io.github.kilmajster.ngrok;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static io.github.kilmajster.ngrok.NgrokConstants.PROP_NGROK_ENABLED;

@ConditionalOnProperty(name = PROP_NGROK_ENABLED, havingValue = "true")
@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface NgrokComponent { }