package dev.murilofontana.aurumway.payments.application.port.out;

public interface PaymentEventPublisherPort {

    void publishPaymentSucceeded(PaymentSucceededEvent event);
}
