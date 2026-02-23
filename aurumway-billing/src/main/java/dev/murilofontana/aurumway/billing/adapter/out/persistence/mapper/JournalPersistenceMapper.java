package dev.murilofontana.aurumway.billing.adapter.out.persistence.mapper;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.JournalEntryEntity;
import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.JournalLineEntity;
import dev.murilofontana.aurumway.billing.domain.model.JournalEntry;
import dev.murilofontana.aurumway.billing.domain.model.JournalLine;
import dev.murilofontana.aurumway.billing.domain.valueobject.AccountCode;
import dev.murilofontana.aurumway.billing.domain.valueobject.EntryType;
import dev.murilofontana.aurumway.billing.domain.valueobject.JournalEntryId;
import dev.murilofontana.aurumway.billing.domain.valueobject.JournalLineId;

import java.util.ArrayList;

public final class JournalPersistenceMapper {

    private JournalPersistenceMapper() {}

    public static JournalEntryEntity toEntity(JournalEntry entry) {
        var lineEntities = new ArrayList<JournalLineEntity>();
        for (var line : entry.lines()) {
            lineEntities.add(new JournalLineEntity(
                    line.id().value(),
                    line.accountCode().name(),
                    line.debit(),
                    line.credit()
            ));
        }
        return new JournalEntryEntity(
                entry.id().value(), entry.entryNumber(), entry.entryType().name(),
                entry.referenceId(), entry.currency(), entry.description(),
                entry.createdAt(), lineEntities
        );
    }

    public static JournalEntry toDomain(JournalEntryEntity entity) {
        var lines = entity.getLines().stream()
                .map(JournalPersistenceMapper::lineToDomain)
                .toList();

        return JournalEntry.rehydrate(
                new JournalEntryId(entity.getEntryId()),
                entity.getEntryNumber(),
                EntryType.valueOf(entity.getEntryType()),
                entity.getReferenceId(),
                entity.getCurrency(),
                entity.getDescription(),
                lines,
                entity.getCreatedAt()
        );
    }

    private static JournalLine lineToDomain(JournalLineEntity entity) {
        return JournalLine.rehydrate(
                new JournalLineId(entity.getLineId()),
                AccountCode.valueOf(entity.getAccountCode()),
                entity.getDebit(),
                entity.getCredit()
        );
    }
}
