package dev.murilofontana.aurumway.contracts.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContractItemTest {

    @Test
    void computesLineTotalWithTax() {
        var item = ContractItem.create("Seat license", 5, new BigDecimal("20.00"), new BigDecimal("0.10"));

        assertThat(item.lineTotal()).isEqualByComparingTo("110.00");
    }

    @Test
    void computesLineTotalWithoutTax() {
        var item = ContractItem.create("Seat license", 2, new BigDecimal("50.00"), BigDecimal.ZERO);

        assertThat(item.lineTotal()).isEqualByComparingTo("100.00");
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> ContractItem.create("X", 0, new BigDecimal("10.00"), BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity");
    }

    @Test
    void rejectsNonPositiveUnitPrice() {
        assertThatThrownBy(() -> ContractItem.create("X", 1, BigDecimal.ZERO, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unit price");
    }

    @Test
    void rejectsNegativeTaxRate() {
        assertThatThrownBy(() -> ContractItem.create("X", 1, new BigDecimal("10.00"), new BigDecimal("-0.05")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tax rate");
    }
}
