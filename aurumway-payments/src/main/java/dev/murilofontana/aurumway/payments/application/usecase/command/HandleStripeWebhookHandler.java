package dev.murilofontana.aurumway.payments.application.usecase.command;

import dev.murilofontana.aurumway.payments.application.port.in.HandleStripeWebhookUseCase;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HandleStripeWebhookHandler implements HandleStripeWebhookUseCase {

    private final PaymentRepositoryPort repository;

    public HandleStripeWebhookHandler(PaymentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void execute(String paymentIntentId, String eventType) {
        var payment = repository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No payment found for PaymentIntent: " + paymentIntentId));

        switch (eventType) {
            case "payment_intent.succeeded" -> payment.markSucceeded();
            case "payment_intent.payment_failed" -> payment.markFailed();
            default -> { return; }
        }

        repository.save(payment);
    }
}
