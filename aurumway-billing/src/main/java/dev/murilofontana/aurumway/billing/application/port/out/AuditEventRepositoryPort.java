package dev.murilofontana.aurumway.billing.application.port.out;

import dev.murilofontana.aurumway.billing.domain.model.AuditEvent;

import java.time.Instant;
import java.util.List;

public interface AuditEventRepositoryPort {

    void save(AuditEvent event);

    List<AuditEvent> findWithFilters(String action, String entityType, String entityId,
                                     String actor, Instant from, Instant to);
}
