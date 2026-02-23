package dev.murilofontana.aurumway.billing.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface GetStatementUseCase {

    GetStatementResult execute(String statementId);

    record GetStatementResult(String statementId, String bankAccount, String filename,
                              Instant importedAt, String status,
                              List<TransactionDetail> transactions) {}

    record TransactionDetail(String transactionId, LocalDate transactionDate, BigDecimal amount,
                             String currency, String description, String reference,
                             String type, String reconciliationStatus,
                             String matchedInvoiceId) {}
}
