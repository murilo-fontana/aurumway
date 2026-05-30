package dev.murilofontana.aurumway.payments.adapter.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.murilofontana.aurumway.payments.adapter.out.persistence.repository.JpaOutboxRepository;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentSucceededEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Relays pending outbox rows to RabbitMQ. Runs on a fixed delay; a broker outage
 * simply leaves rows PENDING for the next run (at-least-once delivery with retry).
 */
@Component
@ConditionalOnProperty(prefix = "scheduling", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);

    private final JpaOutboxRepository outbox;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String exchange;

    public OutboxRelay(JpaOutboxRepository outbox,
                       RabbitTemplate rabbitTemplate,
                       ObjectMapper objectMapper,
                       @Value("${messaging.payments.exchange}") String exchange) {
        this.outbox = outbox;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.exchange = exchange;
    }

    @Scheduled(fixedDelayString = "${scheduling.outbox.poll-delay-ms}")
    @Transactional
    public void publishPending() {
        var pending = outbox.findTop100ByStatusOrderByCreatedAtAsc("PENDING");
        for (var row : pending) {
            try {
                var event = objectMapper.readValue(row.getPayload(), PaymentSucceededEvent.class);
                rabbitTemplate.convertAndSend(exchange, row.getRoutingKey(), event);
                row.markSent();
            } catch (Exception e) {
                row.recordFailure();
                log.error("Failed to relay outbox event {} (attempt {}): {}",
                        row.getId(), row.getAttempts(), e.getMessage());
            }
        }
    }
}
