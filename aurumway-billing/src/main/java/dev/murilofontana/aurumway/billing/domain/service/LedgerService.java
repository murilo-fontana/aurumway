package dev.murilofontana.aurumway.billing.domain.service;

import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.model.JournalEntry;
import dev.murilofontana.aurumway.billing.domain.model.JournalLine;
import dev.murilofontana.aurumway.billing.domain.valueobject.AccountCode;
import dev.murilofontana.aurumway.billing.domain.valueobject.EntryType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class LedgerService {

    private LedgerService() {}

    public static JournalEntry forInvoiceIssued(String entryNumber, Invoice invoice) {
        var total = invoice.totalAmount().amount();
        var netAmount = calculateNetAmount(invoice);
        var taxAmount = total.subtract(netAmount);

        var lines = new ArrayList<JournalLine>();
        lines.add(JournalLine.debit(AccountCode.ACCOUNTS_RECEIVABLE, total));
        lines.add(JournalLine.credit(AccountCode.REVENUE, netAmount));
        if (taxAmount.signum() > 0) {
            lines.add(JournalLine.credit(AccountCode.TAX_PAYABLE, taxAmount));
        }

        return JournalEntry.create(entryNumber, EntryType.INVOICE_ISSUED, invoice.id().value(),
                invoice.currency(),
                "Invoice %s issued".formatted(invoice.invoiceNumber()),
                lines);
    }

    public static JournalEntry forPaymentReceived(String entryNumber, Invoice invoice) {
        var total = invoice.totalAmount().amount();

        return JournalEntry.create(entryNumber, EntryType.PAYMENT_RECEIVED, invoice.id().value(),
                invoice.currency(),
                "Payment received for invoice %s".formatted(invoice.invoiceNumber()),
                List.of(
                        JournalLine.debit(AccountCode.CASH, total),
                        JournalLine.credit(AccountCode.ACCOUNTS_RECEIVABLE, total)
                ));
    }

    public static JournalEntry forInvoiceCancelled(String entryNumber, Invoice invoice) {
        var total = invoice.totalAmount().amount();
        var netAmount = calculateNetAmount(invoice);
        var taxAmount = total.subtract(netAmount);

        var lines = new ArrayList<JournalLine>();
        lines.add(JournalLine.credit(AccountCode.ACCOUNTS_RECEIVABLE, total));
        lines.add(JournalLine.debit(AccountCode.REVENUE, netAmount));
        if (taxAmount.signum() > 0) {
            lines.add(JournalLine.debit(AccountCode.TAX_PAYABLE, taxAmount));
        }

        return JournalEntry.create(entryNumber, EntryType.INVOICE_CANCELLED, invoice.id().value(),
                invoice.currency(),
                "Invoice %s cancelled (reversal)".formatted(
                        invoice.invoiceNumber() != null ? invoice.invoiceNumber() : invoice.id().value()),
                lines);
    }

    public static JournalEntry forRefundIssued(String entryNumber, Invoice invoice, BigDecimal refundAmount) {
        return JournalEntry.create(entryNumber, EntryType.REFUND_ISSUED, invoice.id().value(),
                invoice.currency(),
                "Refund of %s for invoice %s".formatted(refundAmount, invoice.invoiceNumber()),
                List.of(
                        JournalLine.credit(AccountCode.CASH, refundAmount),
                        JournalLine.debit(AccountCode.ACCOUNTS_RECEIVABLE, refundAmount)
                ));
    }

    private static BigDecimal calculateNetAmount(Invoice invoice) {
        return invoice.lines().stream()
                .map(line -> line.unitPrice()
                        .multiply(BigDecimal.valueOf(line.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
