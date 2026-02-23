package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record GetLedgerEntryResponse(
        String entryId, String entryNumber, String entryType, String referenceId,
        String currency, String description, Instant createdAt, List<LedgerLineResponse> lines
) {
    public record LedgerLineResponse(String lineId, String accountCode, BigDecimal debit, BigDecimal credit) {}
}
