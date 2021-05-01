package ngrok.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrokCapturedRequestsList implements Serializable {

    private String uri;
    private List<NgrokCapturedRequest> requests;

    @Tolerate
    public NgrokCapturedRequestsList() {
    }
}