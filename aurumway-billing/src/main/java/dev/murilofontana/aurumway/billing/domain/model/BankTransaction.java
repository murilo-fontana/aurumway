package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.domain.valueobject.ReconciliationStatus;
import dev.murilofontana.aurumway.billing.domain.valueobject.TransactionId;
import dev.murilofontana.aurumway.billing.domain.valueobject.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class BankTransaction {

    private final TransactionId id;
    private final LocalDate transactionDate;
    private final BigDecimal amount;
    private final String currency;
    private final String description;
    private final String reference;
    private final TransactionType type;
    private ReconciliationStatus reconciliationStatus;
    private String matchedInvoiceId;

    private BankTransaction(TransactionId id, LocalDate transactionDate, BigDecimal amount,
                            String currency, String description, String reference,
                            TransactionType type, ReconciliationStatus reconciliationStatus,
                            String matchedInvoiceId) {
        this.id = Objects.requireNonNull(id);
        this.transactionDate = Objects.requireNonNull(transactionDate);
        this.amount = Objects.requireNonNull(amount);
        this.currency = Objects.requireNonNull(currency);
        this.description = description;
        this.reference = reference;
        this.type = Objects.requireNonNull(type);
        this.reconciliationStatus = Objects.requireNonNull(reconciliationStatus);
        this.matchedInvoiceId = matchedInvoiceId;
    }

    public static BankTransaction create(LocalDate transactionDate, BigDecimal amount,
                                         String currency, String description, String reference,
                                         TransactionType type) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        return new BankTransaction(TransactionId.newId(), transactionDate, amount, currency,
                description, reference, type, ReconciliationStatus.UNMATCHED, null);
    }

    public static BankTransaction rehydrate(TransactionId id, LocalDate transactionDate, BigDecimal amount,
                                            String currency, String description, String reference,
                                            TransactionType type, ReconciliationStatus reconciliationStatus,
                                            String matchedInvoiceId) {
        return new BankTransaction(id, transactionDate, amount, currency, description, reference,
                type, reconciliationStatus, matchedInvoiceId);
    }

    public void matchToInvoice(String invoiceId, ReconciliationStatus status) {
        if (this.reconciliationStatus != ReconciliationStatus.UNMATCHED) {
            throw new IllegalStateException("Transaction %s is already matched".formatted(id.value()));
        }
        this.matchedInvoiceId = Objects.requireNonNull(invoiceId);
        this.reconciliationStatus = status;
    }

    public void unmatch() {
        this.matchedInvoiceId = null;
        this.reconciliationStatus = ReconciliationStatus.UNMATCHED;
    }

    public boolean isUnmatched() {
        return reconciliationStatus == ReconciliationStatus.UNMATCHED;
    }

    public TransactionId id() { return id; }
    public LocalDate transactionDate() { return transactionDate; }
    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }
    public String description() { return description; }
    public String reference() { return reference; }
    public TransactionType type() { return type; }
    public ReconciliationStatus reconciliationStatus() { return reconciliationStatus; }
    public String matchedInvoiceId() { return matchedInvoiceId; }
}
