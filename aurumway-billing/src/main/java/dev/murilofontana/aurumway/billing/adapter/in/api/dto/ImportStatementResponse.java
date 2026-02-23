package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.time.Instant;

public record ImportStatementResponse(String statementId, String status,
                                      int transactionCount, Instant importedAt) {}
