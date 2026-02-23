package dev.murilofontana.aurumway.payments.application.port.in;

public interface HandleStripeWebhookUseCase {

    void execute(String paymentIntentId, String eventType);
}
