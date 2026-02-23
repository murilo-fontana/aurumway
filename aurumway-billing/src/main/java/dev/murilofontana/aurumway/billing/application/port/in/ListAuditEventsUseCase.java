package dev.murilofontana.aurumway.billing.application.port.in;

import java.time.Instant;
import java.util.List;

public interface ListAuditEventsUseCase {

    List<AuditEventResult> execute(String action, String entityType, String entityId,
                                   String actor, Instant from, Instant to);

    record AuditEventResult(String eventId, String tenantId, String actor, String action,
                            String entityType, String entityId, String payload, Instant createdAt) {}
}
