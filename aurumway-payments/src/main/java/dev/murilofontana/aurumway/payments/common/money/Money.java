package dev.murilofontana.aurumway.payments.common.money;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {

    public Money {
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");

        if (currency.isBlank()) throw new IllegalArgumentException("currency is required");
        if (amount.scale() > 2) throw new IllegalArgumentException("amount max scale is 2");
        if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be positive");
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
}