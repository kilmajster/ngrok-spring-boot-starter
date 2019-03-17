package io.github.createam.ngrok.exception;

public class NgrokStartupException extends RuntimeException {
    public NgrokStartupException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
