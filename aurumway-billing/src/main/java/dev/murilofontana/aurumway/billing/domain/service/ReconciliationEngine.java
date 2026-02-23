package dev.murilofontana.aurumway.billing.domain.service;

import dev.murilofontana.aurumway.billing.domain.model.BankStatement;
import dev.murilofontana.aurumway.billing.domain.model.BankTransaction;
import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.valueobject.ReconciliationStatus;
import dev.murilofontana.aurumway.billing.domain.valueobject.TransactionType;

import java.util.ArrayList;
import java.util.List;

public final class ReconciliationEngine {

    private ReconciliationEngine() {}

    public static ReconciliationResult reconcile(BankStatement statement, List<Invoice> openInvoices) {
        var matched = new ArrayList<MatchedPair>();
        var remaining = new ArrayList<>(openInvoices);

        for (var tx : statement.transactions()) {
            if (!tx.isUnmatched() || tx.type() != TransactionType.CREDIT) {
                continue;
            }

            var invoice = findByReference(tx, remaining);
            if (invoice == null) {
                invoice = findByExactAmount(tx, remaining);
            }

            if (invoice != null) {
                tx.matchToInvoice(invoice.id().value(), ReconciliationStatus.AUTO_MATCHED);
                matched.add(new MatchedPair(tx, invoice));
                remaining.remove(invoice);
            }
        }

        statement.refreshStatus();
        return new ReconciliationResult(matched);
    }

    private static Invoice findByReference(BankTransaction tx, List<Invoice> invoices) {
        var searchText = (nullSafe(tx.reference()) + " " + nullSafe(tx.description())).toUpperCase();

        return invoices.stream()
                .filter(inv -> inv.invoiceNumber() != null
                        && searchText.contains(inv.invoiceNumber().toUpperCase())
                        && inv.totalAmount().amount().compareTo(tx.amount()) == 0
                        && inv.currency().equalsIgnoreCase(tx.currency()))
                .findFirst()
                .orElse(null);
    }

    private static Invoice findByExactAmount(BankTransaction tx, List<Invoice> invoices) {
        var candidates = invoices.stream()
                .filter(inv -> inv.totalAmount().amount().compareTo(tx.amount()) == 0
                        && inv.currency().equalsIgnoreCase(tx.currency()))
                .toList();

        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        return null;
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }

    public record ReconciliationResult(List<MatchedPair> matched) {
        public int matchedCount() { return matched.size(); }
    }

    public record MatchedPair(BankTransaction transaction, Invoice invoice) {}
}
