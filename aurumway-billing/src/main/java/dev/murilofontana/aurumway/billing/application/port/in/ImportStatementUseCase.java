package dev.murilofontana.aurumway.billing.application.port.in;

import java.io.InputStream;
import java.time.Instant;

public interface ImportStatementUseCase {

    ImportStatementResult execute(String bankAccount, String filename, InputStream csvData);

    record ImportStatementResult(String statementId, String status, int transactionCount, Instant importedAt) {}
}
