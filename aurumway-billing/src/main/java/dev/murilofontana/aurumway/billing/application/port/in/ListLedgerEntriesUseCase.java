package dev.murilofontana.aurumway.billing.application.port.in;

import java.time.Instant;
import java.util.List;

public interface ListLedgerEntriesUseCase {

    List<LedgerEntrySummary> execute(String entryType, Instant from, Instant to);

    record LedgerEntrySummary(
            String entryId, String entryNumber, String entryType, String referenceId,
            String currency, String description, Instant createdAt
    ) {}
}
