package dev.murilofontana.aurumway.payments.application.usecase.command;

import java.math.BigDecimal;
import java.util.Objects;

public record CreatePaymentCommand(
        String externalReference,
        String provider,
        BigDecimal amount,
        String currency
) {
    public CreatePaymentCommand {
        Objects.requireNonNull(externalReference, "externalReference is required");
        Objects.requireNonNull(provider, "provider is required");
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");

        if (externalReference.isBlank()) throw new IllegalArgumentException("externalReference is blank");
        if (provider.isBlank()) throw new IllegalArgumentException("provider is blank");
        if (currency.isBlank()) throw new IllegalArgumentException("currency is blank");
        if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");
    }
}