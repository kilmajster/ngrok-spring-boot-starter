package ngrok.download;

import lombok.Getter;

@Getter
public class NgrokLegacyV2ArchiveUrls implements NgrokArchiveUrlProvider {
    private final String windows = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-amd64.zip";
    private final String linux = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip";
    private final String osx = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-amd64.zip";
    private final String windows32 = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-windows-386.zip";
    private final String linux32 = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-386.zip";
    private final String osx32 = "https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-darwin-386.zip";
}
