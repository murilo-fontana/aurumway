package dev.murilofontana.aurumway.payments.domain.model;

import dev.murilofontana.aurumway.payments.common.money.Money;
import dev.murilofontana.aurumway.payments.domain.valueobject.PaymentStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    private static Payment pending() {
        return Payment.createPending("order-1", "stripe", Money.of(new BigDecimal("100.00"), "USD"));
    }

    private static Payment succeeded() {
        var payment = pending();
        payment.markProcessing("pi_123");
        payment.markSucceeded();
        return payment;
    }

    @Nested
    class Lifecycle {

        @Test
        void createsPendingWithZeroRefund() {
            var payment = pending();

            assertThat(payment.status()).isEqualTo(PaymentStatus.PENDING);
            assertThat(payment.refundedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(payment.stripePaymentIntentId()).isNull();
        }

        @Test
        void markProcessingStoresPaymentIntentId() {
            var payment = pending();

            payment.markProcessing("pi_abc");

            assertThat(payment.status()).isEqualTo(PaymentStatus.PROCESSING);
            assertThat(payment.stripePaymentIntentId()).isEqualTo("pi_abc");
        }

        @Test
        void fullHappyPath() {
            var payment = succeeded();

            assertThat(payment.status()).isEqualTo(PaymentStatus.SUCCEEDED);
        }

        @Test
        void markSucceededRequiresProcessing() {
            var payment = pending();

            assertThatThrownBy(payment::markSucceeded).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void markFailedFromProcessing() {
            var payment = pending();
            payment.markProcessing("pi_x");

            payment.markFailed();

            assertThat(payment.status()).isEqualTo(PaymentStatus.FAILED);
        }

        @Test
        void cancelOnlyFromPending() {
            var payment = pending();

            payment.cancel();

            assertThat(payment.status()).isEqualTo(PaymentStatus.CANCELLED);
        }

        @Test
        void cannotCancelProcessing() {
            var payment = pending();
            payment.markProcessing("pi_x");

            assertThatThrownBy(payment::cancel).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class Refunds {

        @Test
        void partialRefund() {
            var payment = succeeded();

            payment.refund(new BigDecimal("30.00"));

            assertThat(payment.status()).isEqualTo(PaymentStatus.PARTIALLY_REFUNDED);
            assertThat(payment.refundableBalance()).isEqualByComparingTo("70.00");
        }

        @Test
        void fullRefund() {
            var payment = succeeded();

            payment.refund(new BigDecimal("100.00"));

            assertThat(payment.status()).isEqualTo(PaymentStatus.REFUNDED);
            assertThat(payment.refundableBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void refundExceedingBalanceRejected() {
            var payment = succeeded();

            assertThatThrownBy(() -> payment.refund(new BigDecimal("100.01")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("exceeds refundable balance");
        }

        @Test
        void refundMustBePositive() {
            var payment = succeeded();

            assertThatThrownBy(() -> payment.refund(new BigDecimal("-1.00")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must be positive");
        }

        @Test
        void cannotRefundPendingPayment() {
            var payment = pending();

            assertThatThrownBy(() -> payment.refund(new BigDecimal("10.00")))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
