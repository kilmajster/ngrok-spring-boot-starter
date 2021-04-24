package ngrok.exception;

public class NgrokApiException extends RuntimeException {
    public NgrokApiException(String message, Throwable throwable) {
        super(message, throwable);
    }
}