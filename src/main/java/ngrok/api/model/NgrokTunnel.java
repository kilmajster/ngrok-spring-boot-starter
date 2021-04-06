package ngrok.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NgrokTunnel implements Serializable {

    @JsonProperty("public_url")
    private String publicUrl;

    private String proto;

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    @JsonIgnore
    public boolean isHttps() {
        return "https".equals(proto);
    }

    @JsonIgnore
    public boolean isHttp() {
        return "http".equals(proto);
    }
}