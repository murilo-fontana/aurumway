package dev.murilofontana.aurumway.contracts.application.port.out;

import dev.murilofontana.aurumway.contracts.domain.model.AuditEvent;

import java.time.Instant;
import java.util.List;

public interface AuditEventRepositoryPort {

    void save(AuditEvent event);

    List<AuditEvent> findWithFilters(String action, String entityType, String entityId,
                                     String actor, Instant from, Instant to);
}
