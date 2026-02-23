package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.time.Instant;

public record LedgerEntrySummaryResponse(
        String entryId, String entryNumber, String entryType, String referenceId,
        String currency, String description, Instant createdAt
) {}
