package ngrok.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrokCapturedRequestsList implements Serializable {

    private String uri;
    private List<NgrokCapturedRequest> requests;

    public NgrokCapturedRequestsList() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<NgrokCapturedRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<NgrokCapturedRequest> requests) {
        this.requests = requests;
    }
}