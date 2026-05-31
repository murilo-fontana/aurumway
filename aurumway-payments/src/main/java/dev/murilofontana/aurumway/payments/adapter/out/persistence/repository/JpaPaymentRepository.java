package dev.murilofontana.aurumway.payments.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.payments.adapter.out.persistence.entity.PaymentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, String> {

    Optional<PaymentEntity> findByPaymentId(String paymentId);

    Optional<PaymentEntity> findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Loads a payment with a row-level write lock so concurrent refunds on the
     * same payment serialize, preventing local lost updates / over-refunds that
     * would otherwise only be caught (or missed) by Stripe.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentEntity p where p.paymentId = :paymentId")
    Optional<PaymentEntity> findByPaymentIdForUpdate(@Param("paymentId") String paymentId);
}
