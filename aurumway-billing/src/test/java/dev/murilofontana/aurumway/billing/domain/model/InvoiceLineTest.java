package dev.murilofontana.aurumway.billing.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvoiceLineTest {

    @Test
    void computesLineTotalWithTaxRoundedToTwoDecimals() {
        var line = InvoiceLine.create("Item", 3, new BigDecimal("9.99"), new BigDecimal("0.07"));

        assertThat(line.lineTotal()).isEqualByComparingTo("32.07");
    }

    @Test
    void computesLineTotalWithoutTax() {
        var line = InvoiceLine.create("Item", 2, new BigDecimal("50.00"), BigDecimal.ZERO);

        assertThat(line.lineTotal()).isEqualByComparingTo("100.00");
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> InvoiceLine.create("Item", 0, new BigDecimal("10.00"), BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantity");
    }

    @Test
    void rejectsNonPositiveUnitPrice() {
        assertThatThrownBy(() -> InvoiceLine.create("Item", 1, BigDecimal.ZERO, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unitPrice");
    }

    @Test
    void rejectsNegativeTaxRate() {
        assertThatThrownBy(() -> InvoiceLine.create("Item", 1, new BigDecimal("10.00"), new BigDecimal("-0.10")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("taxRate");
    }
}
