package dev.murilofontana.aurumway.payments.application.port.out;

import dev.murilofontana.aurumway.payments.domain.model.Payment;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentId;

import java.util.Optional;

public interface PaymentRepositoryPort {

    Payment save(Payment payment);

    Optional<Payment> findById(PaymentId id);

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
}
