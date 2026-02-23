package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.GetStatementUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.StatementRepositoryPort;
import dev.murilofontana.aurumway.billing.domain.valueobject.StatementId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetStatementHandler implements GetStatementUseCase {

    private final StatementRepositoryPort repository;

    public GetStatementHandler(StatementRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public GetStatementResult execute(String statementId) {
        var statement = repository.findById(new StatementId(statementId))
                .orElseThrow(() -> new StatementNotFoundException(statementId));

        var txDetails = statement.transactions().stream()
                .map(tx -> new TransactionDetail(
                        tx.id().value(), tx.transactionDate(), tx.amount(),
                        tx.currency(), tx.description(), tx.reference(),
                        tx.type().name(), tx.reconciliationStatus().name(),
                        tx.matchedInvoiceId()))
                .toList();

        return new GetStatementResult(
                statement.id().value(), statement.bankAccount(), statement.filename(),
                statement.importedAt(), statement.status().name(), txDetails
        );
    }
}
