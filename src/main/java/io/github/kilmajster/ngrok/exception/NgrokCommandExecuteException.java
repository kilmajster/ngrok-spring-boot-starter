package io.github.kilmajster.ngrok.exception;

public class NgrokCommandExecuteException extends RuntimeException {
    public NgrokCommandExecuteException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
