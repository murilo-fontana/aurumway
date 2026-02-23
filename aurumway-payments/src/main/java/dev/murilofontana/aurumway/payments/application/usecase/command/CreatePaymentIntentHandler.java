package dev.murilofontana.aurumway.payments.application.usecase.command;

import dev.murilofontana.aurumway.payments.application.port.in.CreatePaymentIntentUseCase;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentRepositoryPort;
import dev.murilofontana.aurumway.payments.application.port.out.StripePaymentPort;
import dev.murilofontana.aurumway.payments.common.money.Money;
import dev.murilofontana.aurumway.payments.domain.model.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePaymentIntentHandler implements CreatePaymentIntentUseCase {

    private final PaymentRepositoryPort repository;
    private final StripePaymentPort stripe;

    public CreatePaymentIntentHandler(PaymentRepositoryPort repository, StripePaymentPort stripe) {
        this.repository = repository;
        this.stripe = stripe;
    }

    @Override
    @Transactional
    public CreatePaymentIntentResult execute(CreatePaymentIntentCommand command) {
        var money = Money.of(command.amount(), command.currency());
        var payment = Payment.createPending(command.externalReference(), "stripe", money);

        var stripeResult = stripe.createPaymentIntent(money, command.externalReference(), command.idempotencyKey());

        payment.markProcessing(stripeResult.paymentIntentId());
        repository.save(payment);

        return new CreatePaymentIntentResult(
                payment.id().value(),
                stripeResult.clientSecret(),
                payment.status().name()
        );
    }
}
