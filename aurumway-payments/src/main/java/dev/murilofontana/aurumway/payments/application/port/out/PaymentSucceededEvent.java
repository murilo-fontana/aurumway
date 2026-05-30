package dev.murilofontana.aurumway.payments.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain event published when a payment transitions to SUCCEEDED.
 * {@code externalReference} carries the billing invoice id this payment settles.
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
