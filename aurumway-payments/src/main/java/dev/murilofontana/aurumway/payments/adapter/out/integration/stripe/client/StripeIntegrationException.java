package dev.murilofontana.aurumway.payments.adapter.out.integration.stripe.client;

public class StripeIntegrationException extends RuntimeException {

    public StripeIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
