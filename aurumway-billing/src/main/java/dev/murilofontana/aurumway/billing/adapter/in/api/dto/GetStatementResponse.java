package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record GetStatementResponse(String statementId, String bankAccount, String filename,
                                   Instant importedAt, String status,
                                   List<TransactionDto> transactions) {

    public record TransactionDto(String transactionId, LocalDate transactionDate, BigDecimal amount,
                                 String currency, String description, String reference,
                                 String type, String reconciliationStatus,
                                 String matchedInvoiceId) {}
}
