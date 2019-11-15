package io.github.kilmajster.ngrok.exception;

public class NgrokDownloadException extends RuntimeException {
    public NgrokDownloadException(Throwable throwable) {
        super(throwable);
    }

    public NgrokDownloadException(String message) {
        super(message);
    }
}
