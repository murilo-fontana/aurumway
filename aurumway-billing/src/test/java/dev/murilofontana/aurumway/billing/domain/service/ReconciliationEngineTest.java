package dev.murilofontana.aurumway.billing.domain.service;

import dev.murilofontana.aurumway.billing.domain.model.BankStatement;
import dev.murilofontana.aurumway.billing.domain.model.BankTransaction;
import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.model.InvoiceLine;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;
import dev.murilofontana.aurumway.billing.domain.valueobject.ReconciliationStatus;
import dev.murilofontana.aurumway.billing.domain.valueobject.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReconciliationEngineTest {

    private static Invoice issuedInvoice(String number, String amount) {
        var line = InvoiceLine.create("Item", 1, new BigDecimal(amount), BigDecimal.ZERO);
        var invoice = Invoice.createDraft(new CustomerId("c"), "USD", List.of(line));
        invoice.issue(number, LocalDate.now().plusDays(30));
        return invoice;
    }

    private static BankTransaction credit(String amount, String description, String reference) {
        return BankTransaction.create(LocalDate.now(), new BigDecimal(amount), "USD",
                description, reference, TransactionType.CREDIT);
    }

    @Test
    void matchesByInvoiceNumberInReference() {
        var invoice = issuedInvoice("INV-001", "100.00");
        var tx = credit("100.00", "Payment", "INV-001");
        var statement = BankStatement.create("ACC", "file.csv", List.of(tx));

        var result = ReconciliationEngine.reconcile(statement, List.of(invoice));

        assertThat(result.matchedCount()).isEqualTo(1);
        assertThat(tx.reconciliationStatus()).isEqualTo(ReconciliationStatus.AUTO_MATCHED);
        assertThat(tx.matchedInvoiceId()).isEqualTo(invoice.id().value());
    }

    @Test
    void matchesByExactAmountWhenSingleCandidate() {
        var invoice = issuedInvoice("INV-002", "250.00");
        var tx = credit("250.00", "Wire transfer", null);
        var statement = BankStatement.create("ACC", "file.csv", List.of(tx));

        var result = ReconciliationEngine.reconcile(statement, List.of(invoice));

        assertThat(result.matchedCount()).isEqualTo(1);
    }

    @Test
    void doesNotMatchAmbiguousAmountWithMultipleCandidates() {
        var inv1 = issuedInvoice("INV-003", "100.00");
        var inv2 = issuedInvoice("INV-004", "100.00");
        var tx = credit("100.00", "Payment", null);
        var statement = BankStatement.create("ACC", "file.csv", List.of(tx));

        var result = ReconciliationEngine.reconcile(statement, List.of(inv1, inv2));

        assertThat(result.matchedCount()).isZero();
        assertThat(tx.isUnmatched()).isTrue();
    }

    @Test
    void ignoresDebitTransactions() {
        var invoice = issuedInvoice("INV-005", "100.00");
        var debit = BankTransaction.create(LocalDate.now(), new BigDecimal("100.00"), "USD",
                "Outgoing", "INV-005", TransactionType.DEBIT);
        var statement = BankStatement.create("ACC", "file.csv", List.of(debit));

        var result = ReconciliationEngine.reconcile(statement, List.of(invoice));

        assertThat(result.matchedCount()).isZero();
    }

    @Test
    void doesNotMatchWhenAmountDiffers() {
        var invoice = issuedInvoice("INV-006", "100.00");
        var tx = credit("99.00", "Payment", "INV-006");
        var statement = BankStatement.create("ACC", "file.csv", List.of(tx));

        var result = ReconciliationEngine.reconcile(statement, List.of(invoice));

        assertThat(result.matchedCount()).isZero();
    }

    @Test
    void doesNotReuseAlreadyMatchedInvoice() {
        var invoice = issuedInvoice("INV-007", "100.00");
        var tx1 = credit("100.00", "Payment", "INV-007");
        var tx2 = credit("100.00", "Payment again", "INV-007");
        var statement = BankStatement.create("ACC", "file.csv", List.of(tx1, tx2));

        var result = ReconciliationEngine.reconcile(statement, List.of(invoice));

        assertThat(result.matchedCount()).isEqualTo(1);
    }
}
