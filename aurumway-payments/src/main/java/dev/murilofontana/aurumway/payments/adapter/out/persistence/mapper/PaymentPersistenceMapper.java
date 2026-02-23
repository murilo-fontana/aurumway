package dev.murilofontana.aurumway.payments.adapter.out.persistence.mapper;

import dev.murilofontana.aurumway.payments.adapter.out.persistence.entity.PaymentEntity;
import dev.murilofontana.aurumway.payments.common.money.Money;
import dev.murilofontana.aurumway.payments.domain.model.Payment;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentId;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentStatus;

public final class PaymentPersistenceMapper {

    private PaymentPersistenceMapper() {}

    public static PaymentEntity toEntity(Payment payment) {
        return new PaymentEntity(
                payment.id().value(),
                payment.externalReference(),
                payment.provider(),
                payment.money().amount(),
                payment.money().currency(),
                payment.status().name(),
                payment.stripePaymentIntentId(),
                payment.refundedAmount(),
                payment.createdAt()
        );
    }

    public static Payment toDomain(PaymentEntity entity) {
        return Payment.rehydrate(
                new PaymentId(entity.getPaymentId()),
                entity.getExternalReference(),
                entity.getProvider(),
                Money.of(entity.getAmount(), entity.getCurrency()),
                PaymentStatus.valueOf(entity.getStatus()),
                entity.getStripePaymentIntentId(),
                entity.getRefundedAmount(),
                entity.getCreatedAt()
        );
    }
}
