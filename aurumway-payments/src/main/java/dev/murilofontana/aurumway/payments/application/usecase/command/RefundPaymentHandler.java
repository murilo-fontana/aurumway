package dev.murilofontana.aurumway.payments.application.usecase.command;

import dev.murilofontana.aurumway.payments.application.port.in.RefundPaymentUseCase;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentRepositoryPort;
import dev.murilofontana.aurumway.payments.application.port.out.StripePaymentPort;
import dev.murilofontana.aurumway.payments.application.usecase.query.PaymentNotFoundException;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RefundPaymentHandler implements RefundPaymentUseCase {

    private final PaymentRepositoryPort repository;
    private final StripePaymentPort stripe;

    public RefundPaymentHandler(PaymentRepositoryPort repository, StripePaymentPort stripe) {
        this.repository = repository;
        this.stripe = stripe;
    }

    @Override
    @Transactional
    public RefundPaymentResult execute(String paymentId, BigDecimal amount, String idempotencyKey) {
        var payment = repository.findById(new PaymentId(paymentId))
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        var refundAmount = amount != null ? amount : payment.refundableBalance();
        var amountInMinorUnits = refundAmount.movePointRight(2).longValueExact();

        var stripeResult = stripe.createRefund(
                payment.stripePaymentIntentId(), amountInMinorUnits, idempotencyKey);

        payment.refund(refundAmount);
        repository.save(payment);

        return new RefundPaymentResult(
                payment.id().value(),
                payment.status().name(),
                payment.refundedAmount(),
                payment.refundableBalance(),
                stripeResult.refundId()
        );
    }
}
