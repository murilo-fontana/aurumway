package dev.murilofontana.aurumway.billing.adapter.in.messaging;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Incoming representation of the payments service {@code payment.succeeded} event.
 * {@code externalReference} carries the billing invoice id to settle.
 */
public record PaymentSucceededEvent(
        String paymentId,
        String externalReference,
        BigDecimal amount,
        String currency,
        String tenantId,
        String stripePaymentIntentId,
        Instant occurredAt) {
}
