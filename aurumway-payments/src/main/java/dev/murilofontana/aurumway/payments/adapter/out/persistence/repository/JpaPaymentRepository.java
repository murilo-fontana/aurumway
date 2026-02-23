package dev.murilofontana.aurumway.payments.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.payments.adapter.out.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, String> {

    Optional<PaymentEntity> findByPaymentId(String paymentId);

    Optional<PaymentEntity> findByStripePaymentIntentId(String stripePaymentIntentId);
}
