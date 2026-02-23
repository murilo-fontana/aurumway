package dev.murilofontana.aurumway.billing.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bank_transactions")
public class BankTransactionEntity {

    @Id
    @Column(name = "transaction_id", nullable = false, updatable = false)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_id", nullable = false)
    private BankStatementEntity statement;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "reference", length = 200)
    private String reference;

    @Column(name = "transaction_type", nullable = false, length = 10)
    private String transactionType;

    @Column(name = "reconciliation_status", nullable = false, length = 20)
    private String reconciliationStatus;

    @Column(name = "matched_invoice_id")
    private String matchedInvoiceId;

    protected BankTransactionEntity() {}

    public BankTransactionEntity(String transactionId, LocalDate transactionDate, BigDecimal amount,
                                 String currency, String description, String reference,
                                 String transactionType, String reconciliationStatus,
                                 String matchedInvoiceId) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.reference = reference;
        this.transactionType = transactionType;
        this.reconciliationStatus = reconciliationStatus;
        this.matchedInvoiceId = matchedInvoiceId;
    }

    public void setStatement(BankStatementEntity statement) { this.statement = statement; }

    public String getTransactionId() { return transactionId; }
    public BankStatementEntity getStatement() { return statement; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public String getReference() { return reference; }
    public String getTransactionType() { return transactionType; }
    public String getReconciliationStatus() { return reconciliationStatus; }
    public String getMatchedInvoiceId() { return matchedInvoiceId; }

    public void setReconciliationStatus(String reconciliationStatus) { this.reconciliationStatus = reconciliationStatus; }
    public void setMatchedInvoiceId(String matchedInvoiceId) { this.matchedInvoiceId = matchedInvoiceId; }
}
