package io.github.createam.ngrok.exception;

public class CommandExecuteException extends RuntimeException {
    public CommandExecuteException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
