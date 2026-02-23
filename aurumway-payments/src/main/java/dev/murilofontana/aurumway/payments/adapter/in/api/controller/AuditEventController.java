package dev.murilofontana.aurumway.payments.adapter.in.api.controller;

import dev.murilofontana.aurumway.payments.application.port.in.ListAuditEventsUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/audit-events")
public class AuditEventController {

    private final ListAuditEventsUseCase listUseCase;

    public AuditEventController(ListAuditEventsUseCase listUseCase) {
        this.listUseCase = listUseCase;
    }

    @GetMapping
    public List<AuditEventResponse> list(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        return listUseCase.execute(action, entityType, entityId, actor, from, to)
                .stream()
                .map(e -> new AuditEventResponse(
                        e.eventId(), e.actor(), e.action(), e.entityType(),
                        e.entityId(), e.payload(), e.createdAt()))
                .toList();
    }

    record AuditEventResponse(String eventId, String actor, String action, String entityType,
                               String entityId, String payload, Instant createdAt) {}
}
