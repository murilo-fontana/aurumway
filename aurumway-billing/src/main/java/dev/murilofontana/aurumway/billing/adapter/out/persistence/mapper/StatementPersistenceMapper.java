package dev.murilofontana.aurumway.billing.adapter.out.persistence.mapper;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.BankStatementEntity;
import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.BankTransactionEntity;
import dev.murilofontana.aurumway.billing.domain.model.BankStatement;
import dev.murilofontana.aurumway.billing.domain.model.BankTransaction;
import dev.murilofontana.aurumway.billing.domain.valueobject.*;

import java.util.ArrayList;

public final class StatementPersistenceMapper {

    private StatementPersistenceMapper() {}

    public static BankStatementEntity toEntity(BankStatement statement) {
        var txEntities = new ArrayList<BankTransactionEntity>();
        for (var tx : statement.transactions()) {
            txEntities.add(new BankTransactionEntity(
                    tx.id().value(), tx.transactionDate(), tx.amount(), tx.currency(),
                    tx.description(), tx.reference(), tx.type().name(),
                    tx.reconciliationStatus().name(), tx.matchedInvoiceId()
            ));
        }
        return new BankStatementEntity(
                statement.id().value(), statement.bankAccount(), statement.filename(),
                statement.importedAt(), statement.status().name(), txEntities
        );
    }

    public static BankStatement toDomain(BankStatementEntity entity) {
        var transactions = entity.getTransactions().stream()
                .map(StatementPersistenceMapper::txToDomain)
                .toList();

        return BankStatement.rehydrate(
                new StatementId(entity.getStatementId()),
                entity.getBankAccount(), entity.getFilename(),
                entity.getImportedAt(),
                StatementStatus.valueOf(entity.getStatus()),
                transactions
        );
    }

    public static void updateEntity(BankStatementEntity entity, BankStatement domain) {
        entity.setStatus(domain.status().name());
        for (var txEntity : entity.getTransactions()) {
            domain.transactions().stream()
                    .filter(tx -> tx.id().value().equals(txEntity.getTransactionId()))
                    .findFirst()
                    .ifPresent(tx -> {
                        txEntity.setReconciliationStatus(tx.reconciliationStatus().name());
                        txEntity.setMatchedInvoiceId(tx.matchedInvoiceId());
                    });
        }
    }

    private static BankTransaction txToDomain(BankTransactionEntity e) {
        return BankTransaction.rehydrate(
                new TransactionId(e.getTransactionId()),
                e.getTransactionDate(), e.getAmount(), e.getCurrency(),
                e.getDescription(), e.getReference(),
                TransactionType.valueOf(e.getTransactionType()),
                ReconciliationStatus.valueOf(e.getReconciliationStatus()),
                e.getMatchedInvoiceId()
        );
    }
}
