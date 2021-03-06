package ngrok;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static ngrok.NgrokProperties.NGROK_ENABLED;

@ConditionalOnProperty(name = NGROK_ENABLED, havingValue = "true")
@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface NgrokComponent {
}