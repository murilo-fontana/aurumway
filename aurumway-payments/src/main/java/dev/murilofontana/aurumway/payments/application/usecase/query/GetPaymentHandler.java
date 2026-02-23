package dev.murilofontana.aurumway.payments.application.usecase.query;

import dev.murilofontana.aurumway.payments.application.port.in.GetPaymentUseCase;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentRepositoryPort;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPaymentHandler implements GetPaymentUseCase {

    private final PaymentRepositoryPort repository;

    public GetPaymentHandler(PaymentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public GetPaymentResult execute(String paymentId) {
        var payment = repository.findById(new PaymentId(paymentId))
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        return new GetPaymentResult(
                payment.id().value(),
                payment.externalReference(),
                payment.provider(),
                payment.money().amount(),
                payment.money().currency(),
                payment.status().name(),
                payment.refundedAmount(),
                payment.createdAt()
        );
    }
}
