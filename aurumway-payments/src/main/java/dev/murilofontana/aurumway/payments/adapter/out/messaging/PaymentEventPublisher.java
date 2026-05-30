package dev.murilofontana.aurumway.payments.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.murilofontana.aurumway.payments.adapter.out.persistence.entity.OutboxEventEntity;
import dev.murilofontana.aurumway.payments.adapter.out.persistence.repository.JpaOutboxRepository;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentEventPublisherPort;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentSucceededEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Persists payment events into the transactional outbox. Running inside the
 * caller's transaction guarantees the event is stored atomically with the
 * domain change; an {@link OutboxRelay} later delivers it to the broker.
 */
@Component
public class PaymentEventPublisher implements PaymentEventPublisherPort {

    private final JpaOutboxRepository outbox;
    private final ObjectMapper objectMapper;
    private final String succeededRoutingKey;

    public PaymentEventPublisher(JpaOutboxRepository outbox,
                                 ObjectMapper objectMapper,
                                 @Value("${messaging.payments.routing-key.succeeded}") String succeededRoutingKey) {
        this.outbox = outbox;
        this.objectMapper = objectMapper;
        this.succeededRoutingKey = succeededRoutingKey;
    }

    @Override
    public void publishPaymentSucceeded(PaymentSucceededEvent event) {
        var entity = new OutboxEventEntity(
                UUID.randomUUID().toString(),
                "Payment",
                event.paymentId(),
                "payment.succeeded",
                succeededRoutingKey,
                serialize(event),
                event.tenantId());
        outbox.save(entity);
    }

    private String serialize(PaymentSucceededEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize payment event", e);
        }
    }
}
