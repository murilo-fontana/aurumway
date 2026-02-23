package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.ListAuditEventsUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.AuditEventRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListAuditEventsHandler implements ListAuditEventsUseCase {

    private final AuditEventRepositoryPort auditRepo;

    public ListAuditEventsHandler(AuditEventRepositoryPort auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Override
    public List<AuditEventResult> execute(String action, String entityType, String entityId,
                                          String actor, Instant from, Instant to) {
        return auditRepo.findWithFilters(action, entityType, entityId, actor, from, to)
                .stream()
                .map(e -> new AuditEventResult(
                        e.eventId(), e.tenantId(), e.actor(), e.action().name(),
                        e.entityType(), e.entityId(), e.payload(), e.createdAt()))
                .toList();
    }
}
