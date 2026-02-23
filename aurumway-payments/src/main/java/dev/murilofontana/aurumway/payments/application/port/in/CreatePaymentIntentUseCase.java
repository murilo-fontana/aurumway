package dev.murilofontana.aurumway.payments.application.port.in;

import dev.murilofontana.aurumway.payments.application.usecase.command.CreatePaymentIntentCommand;

public interface CreatePaymentIntentUseCase {

    CreatePaymentIntentResult execute(CreatePaymentIntentCommand command);

    record CreatePaymentIntentResult(String paymentId, String clientSecret, String status) {}
}
