package dev.murilofontana.aurumway.payments.application.port.in;

import java.math.BigDecimal;

public interface RefundPaymentUseCase {

    RefundPaymentResult execute(String paymentId, BigDecimal amount, String idempotencyKey);

    record RefundPaymentResult(String paymentId, String status, BigDecimal refundedAmount,
                               BigDecimal refundableBalance, String stripeRefundId) {}
}
