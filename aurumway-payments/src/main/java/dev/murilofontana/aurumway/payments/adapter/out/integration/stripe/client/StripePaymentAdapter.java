package dev.murilofontana.aurumway.payments.adapter.out.integration.stripe.client;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import dev.murilofontana.aurumway.payments.application.port.out.StripePaymentPort;
import dev.murilofontana.aurumway.payments.common.money.Money;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentAdapter implements StripePaymentPort {

    @Override
    public StripePaymentIntentResult createPaymentIntent(Money money, String externalReference, String idempotencyKey) {
        try {
            var params = PaymentIntentCreateParams.builder()
                    .setAmount(toMinorUnits(money))
                    .setCurrency(money.currency().toLowerCase())
                    .putMetadata("external_reference", externalReference)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(
                                            PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build()
                    )
                    .build();

            var requestOptions = RequestOptions.builder()
                    .setIdempotencyKey(idempotencyKey)
                    .build();

            var paymentIntent = PaymentIntent.create(params, requestOptions);

            return new StripePaymentIntentResult(paymentIntent.getId(), paymentIntent.getClientSecret());
        } catch (StripeException e) {
            throw new StripeIntegrationException("Failed to create PaymentIntent: " + e.getMessage(), e);
        }
    }

    @Override
    public StripeRefundResult createRefund(String paymentIntentId, long amountInMinorUnits, String idempotencyKey) {
        try {
            var params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .setAmount(amountInMinorUnits)
                    .build();

            var requestOptions = RequestOptions.builder()
                    .setIdempotencyKey(idempotencyKey)
                    .build();

            var refund = Refund.create(params, requestOptions);
            return new StripeRefundResult(refund.getId(), refund.getStatus());
        } catch (StripeException e) {
            throw new StripeIntegrationException("Failed to create Refund: " + e.getMessage(), e);
        }
    }

    private long toMinorUnits(Money money) {
        return money.amount().movePointRight(2).longValueExact();
    }
}
