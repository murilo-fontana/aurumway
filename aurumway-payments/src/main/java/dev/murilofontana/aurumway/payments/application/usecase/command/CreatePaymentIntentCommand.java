package dev.murilofontana.aurumway.payments.application.usecase.command;

import java.math.BigDecimal;
import java.util.Objects;

public record CreatePaymentIntentCommand(
        String externalReference,
        BigDecimal amount,
        String currency,
        String idempotencyKey
) {
    public CreatePaymentIntentCommand {
        Objects.requireNonNull(externalReference, "externalReference is required");
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey is required");

        if (externalReference.isBlank()) throw new IllegalArgumentException("externalReference is blank");
        if (currency.isBlank()) throw new IllegalArgumentException("currency is blank");
        if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");
        if (idempotencyKey.isBlank()) throw new IllegalArgumentException("idempotencyKey is blank");
    }
}
