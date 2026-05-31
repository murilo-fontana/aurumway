package dev.murilofontana.aurumway.payments.application.usecase.command;

import dev.murilofontana.aurumway.payments.application.port.in.HandleStripeWebhookUseCase;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentEventPublisherPort;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentRepositoryPort;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentSucceededEvent;
import dev.murilofontana.aurumway.payments.config.TenantContext;
import dev.murilofontana.aurumway.payments.domain.model.Payment;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class HandleStripeWebhookHandler implements HandleStripeWebhookUseCase {

    private final PaymentRepositoryPort repository;
    private final PaymentEventPublisherPort eventPublisher;

    public HandleStripeWebhookHandler(PaymentRepositoryPort repository, PaymentEventPublisherPort eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void execute(String paymentIntentId, String eventType) {
        var payment = repository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No payment found for PaymentIntent: " + paymentIntentId));

        var tenantId = repository.findTenantIdByStripePaymentIntentId(paymentIntentId).orElse(null);
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
        }

        // Stripe delivers webhooks at-least-once, so the same event may arrive
        // more than once. Only the PROCESSING -> terminal transition is meaningful;
        // a replayed event for an already-settled payment is acknowledged as a no-op.
        if (payment.status() != PaymentStatus.PROCESSING) {
            return;
        }

        switch (eventType) {
            case "payment_intent.succeeded" -> {
                payment.markSucceeded();
                repository.save(payment);
                eventPublisher.publishPaymentSucceeded(toEvent(payment, tenantId));
            }
            case "payment_intent.payment_failed" -> {
                payment.markFailed();
                repository.save(payment);
            }
            default -> { }
        }
    }

    private PaymentSucceededEvent toEvent(Payment payment, String tenantId) {
        return new PaymentSucceededEvent(
                payment.id().value(),
                payment.externalReference(),
                payment.money().amount(),
                payment.money().currency(),
                tenantId,
                payment.stripePaymentIntentId(),
                Instant.now());
    }
}
