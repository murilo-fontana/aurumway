package dev.murilofontana.aurumway.payments.common.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void createsValidMoney() {
        var money = Money.of(new BigDecimal("99.99"), "EUR");

        assertThat(money.amount()).isEqualByComparingTo("99.99");
        assertThat(money.currency()).isEqualTo("EUR");
    }

    @Test
    void rejectsZeroAmount() {
        assertThatThrownBy(() -> Money.of(BigDecimal.ZERO, "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void rejectsScaleGreaterThanTwo() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("1.234"), "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("scale");
    }

    @Test
    void rejectsBlankCurrency() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("10.00"), ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
