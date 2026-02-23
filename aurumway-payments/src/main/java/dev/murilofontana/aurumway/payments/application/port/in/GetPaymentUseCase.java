package dev.murilofontana.aurumway.payments.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;

public interface GetPaymentUseCase {

    GetPaymentResult execute(String paymentId);

    record GetPaymentResult(
            String paymentId,
            String externalReference,
            String provider,
            BigDecimal amount,
            String currency,
            String status,
            BigDecimal refundedAmount,
            Instant createdAt
    ) {}
}
