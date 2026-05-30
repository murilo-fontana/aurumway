package dev.murilofontana.aurumway.billing.adapter.in.messaging;

import dev.murilofontana.aurumway.billing.application.port.in.PayInvoiceUseCase;
import dev.murilofontana.aurumway.billing.application.usecase.query.InvoiceNotFoundException;
import dev.murilofontana.aurumway.billing.config.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code payment.succeeded} events and settles the referenced invoice.
 * Non-retryable conditions (invoice missing or not in a payable state) are logged
 * and acknowledged; unexpected failures propagate so the broker routes the message
 * to the dead-letter queue.
 */
@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final PayInvoiceUseCase payInvoiceUseCase;

    public PaymentEventListener(PayInvoiceUseCase payInvoiceUseCase) {
        this.payInvoiceUseCase = payInvoiceUseCase;
    }

    @RabbitListener(queues = "${messaging.payments.queue}")
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        if (event.tenantId() == null || event.externalReference() == null) {
            log.warn("Discarding payment.succeeded event with missing tenant or reference: paymentId={}",
                    event.paymentId());
            return;
        }

        TenantContext.setCurrentTenant(event.tenantId());
        try {
            var result = payInvoiceUseCase.execute(event.externalReference());
            log.info("Settled invoice {} from payment {} (status={})",
                    event.externalReference(), event.paymentId(), result.status());
        } catch (InvoiceNotFoundException e) {
            log.warn("No invoice {} for payment {}; acknowledging event",
                    event.externalReference(), event.paymentId());
        } catch (IllegalStateException e) {
            log.warn("Invoice {} not settleable for payment {}: {}; acknowledging event",
                    event.externalReference(), event.paymentId(), e.getMessage());
        } finally {
            TenantContext.clear();
        }
    }
}
