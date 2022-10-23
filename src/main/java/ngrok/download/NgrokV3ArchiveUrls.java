package ngrok.download;

import lombok.Getter;
import ngrok.exception.NgrokMalformedConfigurationException;

@Getter
public class NgrokV3ArchiveUrls implements NgrokArchiveUrlProvider {

    private final String windows = "https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-windows-amd64.zip";
    private final String linux = "https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-linux-amd64.tgz";
    private final String osx = "https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-darwin-amd64.zip";
    private final String windows32 = "https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-windows-386.zip";
    private final String linux32 = "https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-linux-386.tgz";

    @Override
    public String getOsx32() {
        throw new NgrokMalformedConfigurationException("Ngrok v3 does not support 32 bit OSX systems.");
    }
}
