package dev.murilofontana.aurumway.billing.common.error;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
