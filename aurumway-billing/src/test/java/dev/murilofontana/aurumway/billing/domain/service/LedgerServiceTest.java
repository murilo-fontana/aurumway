package dev.murilofontana.aurumway.billing.domain.service;

import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.model.InvoiceLine;
import dev.murilofontana.aurumway.billing.domain.model.JournalLine;
import dev.murilofontana.aurumway.billing.domain.valueobject.AccountCode;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;
import dev.murilofontana.aurumway.billing.domain.valueobject.EntryType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LedgerServiceTest {

    private static Invoice issuedInvoice() {
        var line = InvoiceLine.create("Consulting", 1, new BigDecimal("100.00"), new BigDecimal("0.10"));
        var invoice = Invoice.createDraft(new CustomerId("cust-1"), "USD", List.of(line));
        invoice.issue("INV-001", LocalDate.now().plusDays(30));
        return invoice;
    }

    private static BigDecimal debitFor(List<JournalLine> lines, AccountCode account) {
        return lines.stream().filter(l -> l.accountCode() == account)
                .map(JournalLine::debit).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal creditFor(List<JournalLine> lines, AccountCode account) {
        return lines.stream().filter(l -> l.accountCode() == account)
                .map(JournalLine::credit).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Test
    void invoiceIssuedDebitsReceivableCreditsRevenueAndTax() {
        var entry = LedgerService.forInvoiceIssued("JE-001", issuedInvoice());

        assertThat(entry.entryType()).isEqualTo(EntryType.INVOICE_ISSUED);
        assertThat(debitFor(entry.lines(), AccountCode.ACCOUNTS_RECEIVABLE)).isEqualByComparingTo("110.00");
        assertThat(creditFor(entry.lines(), AccountCode.REVENUE)).isEqualByComparingTo("100.00");
        assertThat(creditFor(entry.lines(), AccountCode.TAX_PAYABLE)).isEqualByComparingTo("10.00");
    }

    @Test
    void invoiceIssuedWithoutTaxOmitsTaxLine() {
        var line = InvoiceLine.create("Item", 1, new BigDecimal("100.00"), BigDecimal.ZERO);
        var invoice = Invoice.createDraft(new CustomerId("c"), "USD", List.of(line));
        invoice.issue("INV-002", LocalDate.now().plusDays(30));

        var entry = LedgerService.forInvoiceIssued("JE-002", invoice);

        assertThat(entry.lines()).hasSize(2);
        assertThat(creditFor(entry.lines(), AccountCode.TAX_PAYABLE)).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void paymentReceivedDebitsCashCreditsReceivable() {
        var entry = LedgerService.forPaymentReceived("JE-003", issuedInvoice());

        assertThat(entry.entryType()).isEqualTo(EntryType.PAYMENT_RECEIVED);
        assertThat(debitFor(entry.lines(), AccountCode.CASH)).isEqualByComparingTo("110.00");
        assertThat(creditFor(entry.lines(), AccountCode.ACCOUNTS_RECEIVABLE)).isEqualByComparingTo("110.00");
    }

    @Test
    void invoiceCancelledReversesIssuedEntry() {
        var entry = LedgerService.forInvoiceCancelled("JE-004", issuedInvoice());

        assertThat(entry.entryType()).isEqualTo(EntryType.INVOICE_CANCELLED);
        assertThat(creditFor(entry.lines(), AccountCode.ACCOUNTS_RECEIVABLE)).isEqualByComparingTo("110.00");
        assertThat(debitFor(entry.lines(), AccountCode.REVENUE)).isEqualByComparingTo("100.00");
        assertThat(debitFor(entry.lines(), AccountCode.TAX_PAYABLE)).isEqualByComparingTo("10.00");
    }

    @Test
    void refundIssuedCreditsCashDebitsReceivable() {
        var entry = LedgerService.forRefundIssued("JE-005", issuedInvoice(), new BigDecimal("40.00"));

        assertThat(entry.entryType()).isEqualTo(EntryType.REFUND_ISSUED);
        assertThat(creditFor(entry.lines(), AccountCode.CASH)).isEqualByComparingTo("40.00");
        assertThat(debitFor(entry.lines(), AccountCode.ACCOUNTS_RECEIVABLE)).isEqualByComparingTo("40.00");
    }
}
