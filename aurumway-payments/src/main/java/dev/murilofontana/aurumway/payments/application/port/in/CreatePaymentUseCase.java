package dev.murilofontana.aurumway.payments.application.port.in;

import dev.murilofontana.aurumway.payments.application.usecase.command.CreatePaymentCommand;

public interface CreatePaymentUseCase {
  CreatePaymentResult create(CreatePaymentCommand command);

  record CreatePaymentResult(String paymentId, String status) {}
}