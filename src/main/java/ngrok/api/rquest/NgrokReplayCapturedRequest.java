package ngrok.api.rquest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class NgrokReplayCapturedRequest {
    private String id;
    @JsonProperty("tunnel_name")
    private String tunnelName;
}