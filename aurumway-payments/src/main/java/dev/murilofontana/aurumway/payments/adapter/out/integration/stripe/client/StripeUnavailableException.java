package dev.murilofontana.aurumway.payments.adapter.out.integration.stripe.client;

/**
 * Raised for transient Stripe failures (network issues, 5xx responses, rate
 * limiting) that are safe to retry. Stripe calls carry an idempotency key, so
 * replays do not create duplicate charges or refunds.
 */
public class StripeUnavailableException extends RuntimeException {

    public StripeUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
