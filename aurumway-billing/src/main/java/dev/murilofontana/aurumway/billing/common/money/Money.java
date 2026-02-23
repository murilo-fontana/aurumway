package dev.murilofontana.aurumway.billing.common.money;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {

    public Money {
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");
        if (currency.isBlank()) throw new IllegalArgumentException("currency is required");
        if (amount.scale() > 2) throw new IllegalArgumentException("amount max scale is 2");
        if (amount.signum() < 0) throw new IllegalArgumentException("amount must not be negative");
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on different currencies: %s vs %s"
                    .formatted(this.currency, other.currency));
        }
    }
}
