package dev.murilofontana.aurumway.billing.application.port.in;

import java.time.Instant;
import java.util.List;

public interface ListStatementsUseCase {

    List<StatementSummary> execute();

    record StatementSummary(String statementId, String bankAccount, String filename,
                            Instant importedAt, String status, int transactionCount) {}
}
