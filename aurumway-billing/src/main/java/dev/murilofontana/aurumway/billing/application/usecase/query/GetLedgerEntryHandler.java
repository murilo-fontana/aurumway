package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.GetLedgerEntryUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.LedgerRepositoryPort;
import dev.murilofontana.aurumway.billing.domain.valueobject.JournalEntryId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetLedgerEntryHandler implements GetLedgerEntryUseCase {

    private final LedgerRepositoryPort repository;

    public GetLedgerEntryHandler(LedgerRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public GetLedgerEntryResult execute(String entryId) {
        var entry = repository.findById(new JournalEntryId(entryId))
                .orElseThrow(() -> new JournalEntryNotFoundException(entryId));

        var lines = entry.lines().stream()
                .map(l -> new LedgerLineResult(l.id().value(), l.accountCode().name(), l.debit(), l.credit()))
                .toList();

        return new GetLedgerEntryResult(
                entry.id().value(), entry.entryNumber(), entry.entryType().name(),
                entry.referenceId(), entry.currency(), entry.description(),
                entry.createdAt(), lines
        );
    }
}
