package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.domain.valueobject.ReconciliationStatus;
import dev.murilofontana.aurumway.billing.domain.valueobject.StatementId;
import dev.murilofontana.aurumway.billing.domain.valueobject.StatementStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class BankStatement {

    private final StatementId id;
    private final String bankAccount;
    private final String filename;
    private final Instant importedAt;
    private StatementStatus status;
    private final List<BankTransaction> transactions;

    private BankStatement(StatementId id, String bankAccount, String filename,
                          Instant importedAt, StatementStatus status,
                          List<BankTransaction> transactions) {
        this.id = Objects.requireNonNull(id);
        this.bankAccount = Objects.requireNonNull(bankAccount);
        this.filename = Objects.requireNonNull(filename);
        this.importedAt = Objects.requireNonNull(importedAt);
        this.status = Objects.requireNonNull(status);
        this.transactions = new ArrayList<>(Objects.requireNonNull(transactions));
    }

    public static BankStatement create(String bankAccount, String filename,
                                       List<BankTransaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Statement must have at least one transaction");
        }
        return new BankStatement(StatementId.newId(), bankAccount, filename,
                Instant.now(), StatementStatus.IMPORTED, transactions);
    }

    public static BankStatement rehydrate(StatementId id, String bankAccount, String filename,
                                          Instant importedAt, StatementStatus status,
                                          List<BankTransaction> transactions) {
        return new BankStatement(id, bankAccount, filename, importedAt, status, transactions);
    }

    public void refreshStatus() {
        long unmatched = transactions.stream()
                .filter(BankTransaction::isUnmatched)
                .count();

        if (unmatched == 0) {
            this.status = StatementStatus.FULLY_RECONCILED;
        } else if (unmatched < transactions.size()) {
            this.status = StatementStatus.PARTIALLY_RECONCILED;
        } else {
            this.status = StatementStatus.IMPORTED;
        }
    }

    public StatementId id() { return id; }
    public String bankAccount() { return bankAccount; }
    public String filename() { return filename; }
    public Instant importedAt() { return importedAt; }
    public StatementStatus status() { return status; }
    public List<BankTransaction> transactions() { return Collections.unmodifiableList(transactions); }
}
