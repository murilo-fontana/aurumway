package dev.murilofontana.aurumway.billing.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Entity
@Table(name = "bank_statements")
public class BankStatementEntity {

    @Id
    @Column(name = "statement_id", nullable = false, updatable = false)
    private String statementId;

    @Column(name = "bank_account", nullable = false, length = 100)
    private String bankAccount;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "imported_at", nullable = false, updatable = false)
    private Instant importedAt;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BankTransactionEntity> transactions = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (this.tenantId == null) {
            this.tenantId = dev.murilofontana.aurumway.billing.config.TenantContext.getCurrentTenant();
        }
    }

    protected BankStatementEntity() {}

    public BankStatementEntity(String statementId, String bankAccount, String filename,
                               Instant importedAt, String status, List<BankTransactionEntity> transactions) {
        this.statementId = statementId;
        this.bankAccount = bankAccount;
        this.filename = filename;
        this.importedAt = importedAt;
        this.status = status;
        this.transactions = transactions;
        transactions.forEach(t -> t.setStatement(this));
    }

    public String getTenantId() { return tenantId; }
    public String getStatementId() { return statementId; }
    public String getBankAccount() { return bankAccount; }
    public String getFilename() { return filename; }
    public Instant getImportedAt() { return importedAt; }
    public String getStatus() { return status; }
    public List<BankTransactionEntity> getTransactions() { return transactions; }

    public void setStatus(String status) { this.status = status; }
}
