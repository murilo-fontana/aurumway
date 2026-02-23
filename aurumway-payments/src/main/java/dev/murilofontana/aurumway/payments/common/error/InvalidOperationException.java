package dev.murilofontana.aurumway.payments.common.error;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
