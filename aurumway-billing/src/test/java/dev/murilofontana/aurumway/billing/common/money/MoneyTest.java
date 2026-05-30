package dev.murilofontana.aurumway.billing.common.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void createsValidMoney() {
        var money = Money.of(new BigDecimal("10.50"), "USD");

        assertThat(money.amount()).isEqualByComparingTo("10.50");
        assertThat(money.currency()).isEqualTo("USD");
    }

    @Test
    void addsSameCurrency() {
        var result = Money.of(new BigDecimal("10.00"), "USD")
                .add(Money.of(new BigDecimal("5.50"), "USD"));

        assertThat(result.amount()).isEqualByComparingTo("15.50");
    }

    @Test
    void rejectsAddingDifferentCurrencies() {
        var usd = Money.of(new BigDecimal("10.00"), "USD");
        var eur = Money.of(new BigDecimal("10.00"), "EUR");

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different currencies");
    }

    @Test
    void rejectsScaleGreaterThanTwo() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("10.123"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("scale");
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-1.00"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
    }

    @Test
    void rejectsBlankCurrency() {
        assertThatThrownBy(() -> Money.of(BigDecimal.ZERO, " "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
