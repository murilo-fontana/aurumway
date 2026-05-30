package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.domain.valueobject.AccountCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JournalLineTest {

    @Test
    void debitLineHasZeroCredit() {
        var line = JournalLine.debit(AccountCode.CASH, new BigDecimal("100.00"));

        assertThat(line.debit()).isEqualByComparingTo("100.00");
        assertThat(line.credit()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void creditLineHasZeroDebit() {
        var line = JournalLine.credit(AccountCode.REVENUE, new BigDecimal("100.00"));

        assertThat(line.credit()).isEqualByComparingTo("100.00");
        assertThat(line.debit()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void rejectsLineWithoutAmount() {
        assertThatThrownBy(() -> JournalLine.debit(AccountCode.CASH, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("either debit or credit");
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> JournalLine.debit(AccountCode.CASH, new BigDecimal("-1.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non-negative");
    }
}
