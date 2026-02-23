package dev.murilofontana.aurumway.payments.application.usecase.command;

import dev.murilofontana.aurumway.payments.application.port.in.CreatePaymentUseCase;
import dev.murilofontana.aurumway.payments.application.port.out.PaymentRepositoryPort;
import dev.murilofontana.aurumway.payments.common.money.Money;
import dev.murilofontana.aurumway.payments.domain.model.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePaymentHandler implements CreatePaymentUseCase {

    private final PaymentRepositoryPort repository;

    public CreatePaymentHandler(PaymentRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CreatePaymentResult create(CreatePaymentCommand command) {
        var money = Money.of(command.amount(), command.currency());
        var payment = Payment.createPending(command.externalReference(), command.provider(), money);

        var saved = repository.save(payment);

        return new CreatePaymentResult(saved.id().value(), saved.status().name());
    }
}
