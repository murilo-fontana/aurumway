package dev.murilofontana.aurumway.billing.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface GetLedgerEntryUseCase {

    GetLedgerEntryResult execute(String entryId);

    record GetLedgerEntryResult(
            String entryId, String entryNumber, String entryType, String referenceId,
            String currency, String description, Instant createdAt, List<LedgerLineResult> lines
    ) {}

    record LedgerLineResult(String lineId, String accountCode, BigDecimal debit, BigDecimal credit) {}
}
