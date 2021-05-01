package ngrok.api.rquest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class NgrokStartTunnel {
    private String addr;
    private String proto;
    private String name;
}