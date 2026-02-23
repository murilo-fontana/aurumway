package dev.murilofontana.aurumway.payments.application.port.out;

import dev.murilofontana.aurumway.payments.common.money.Money;

public interface StripePaymentPort {

    StripePaymentIntentResult createPaymentIntent(Money money, String externalReference, String idempotencyKey);

    StripeRefundResult createRefund(String paymentIntentId, long amountInMinorUnits, String idempotencyKey);

    record StripePaymentIntentResult(String paymentIntentId, String clientSecret) {}

    record StripeRefundResult(String refundId, String status) {}
}
