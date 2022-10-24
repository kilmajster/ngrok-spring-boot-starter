package ngrok.download;

import ngrok.NgrokComponent;

@NgrokComponent
public interface NgrokArchiveUrlProvider {

    String getWindows();

    String getLinux();

    String getOsx();

    String getWindows32();

    String getLinux32();

    String getOsx32();

}
