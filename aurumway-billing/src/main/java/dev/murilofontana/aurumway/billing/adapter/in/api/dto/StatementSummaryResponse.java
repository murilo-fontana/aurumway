package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.time.Instant;

public record StatementSummaryResponse(String statementId, String bankAccount, String filename,
                                       Instant importedAt, String status, int transactionCount) {}
