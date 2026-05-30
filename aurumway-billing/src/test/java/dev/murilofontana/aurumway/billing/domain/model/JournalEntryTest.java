package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.domain.valueobject.AccountCode;
import dev.murilofontana.aurumway.billing.domain.valueobject.EntryType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JournalEntryTest {

    @Test
    void createsBalancedEntry() {
        var entry = JournalEntry.create("JE-001", EntryType.INVOICE_ISSUED, "inv-1", "USD", "test",
                List.of(
                        JournalLine.debit(AccountCode.ACCOUNTS_RECEIVABLE, new BigDecimal("110.00")),
                        JournalLine.credit(AccountCode.REVENUE, new BigDecimal("100.00")),
                        JournalLine.credit(AccountCode.TAX_PAYABLE, new BigDecimal("10.00"))
                ));

        assertThat(entry.lines()).hasSize(3);
        assertThat(entry.entryType()).isEqualTo(EntryType.INVOICE_ISSUED);
    }

    @Test
    void rejectsUnbalancedEntry() {
        assertThatThrownBy(() -> JournalEntry.create("JE-001", EntryType.INVOICE_ISSUED, "inv-1", "USD", "test",
                List.of(
                        JournalLine.debit(AccountCode.ACCOUNTS_RECEIVABLE, new BigDecimal("110.00")),
                        JournalLine.credit(AccountCode.REVENUE, new BigDecimal("100.00"))
                )))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not balanced");
    }

    @Test
    void rejectsFewerThanTwoLines() {
        assertThatThrownBy(() -> JournalEntry.create("JE-001", EntryType.INVOICE_ISSUED, "inv-1", "USD", "test",
                List.of(JournalLine.debit(AccountCode.CASH, new BigDecimal("100.00")))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 2 lines");
    }
}
