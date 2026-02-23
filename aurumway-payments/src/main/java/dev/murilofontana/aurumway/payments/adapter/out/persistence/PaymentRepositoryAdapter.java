package dev.murilofontana.aurumway.payments.adapter.out.persistence;

import dev.murilofontana.aurumway.payments.adapter.out.persistence.mapper.PaymentPersistenceMapper;
import dev.murilofontana.aurumway.payments.adapter.out.persistence.repository.JpaPaymentRepository;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentRepositoryPort;
import dev.murilofontana.aurumway.payments.config.TenantFilterSupport;
import dev.murilofontana.aurumway.payments.domain.model.Payment;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final JpaPaymentRepository jpa;
    private final TenantFilterSupport tenantFilter;

    public PaymentRepositoryAdapter(JpaPaymentRepository jpa, TenantFilterSupport tenantFilter) {
        this.jpa = jpa;
        this.tenantFilter = tenantFilter;
    }

    @Override
    public Payment save(Payment payment) {
        tenantFilter.enableTenantFilter();
        var existing = jpa.findById(payment.id().value());
        if (existing.isPresent()) {
            var entity = existing.get();
            entity.setStatus(payment.status().name());
            entity.setStripePaymentIntentId(payment.stripePaymentIntentId());
            entity.setRefundedAmount(payment.refundedAmount());
            return PaymentPersistenceMapper.toDomain(entity);
        }
        var entity = PaymentPersistenceMapper.toEntity(payment);
        var saved = jpa.save(entity);
        return PaymentPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        tenantFilter.enableTenantFilter();
        return jpa.findByPaymentId(id.value()).map(PaymentPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId) {
        // No tenant filter - webhooks are cross-tenant
        return jpa.findByStripePaymentIntentId(stripePaymentIntentId)
                .map(PaymentPersistenceMapper::toDomain);
    }
}
