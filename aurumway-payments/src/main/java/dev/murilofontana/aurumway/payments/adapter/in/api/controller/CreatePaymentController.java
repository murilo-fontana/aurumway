package dev.murilofontana.aurumway.payments.adapter.in.api.controller;

import dev.murilofontana.aurumway.payments.adapter.in.api.dto.CreatePaymentRequest;
import dev.murilofontana.aurumway.payments.adapter.in.api.dto.CreatePaymentResponse;
import dev.murilofontana.aurumway.payments.application.port.in.CreatePaymentUseCase;
import dev.murilofontana.aurumway.payments.application.usecase.command.CreatePaymentCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class CreatePaymentController {

    private final CreatePaymentUseCase useCase;

    public CreatePaymentController(CreatePaymentUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePaymentResponse create(@Valid @RequestBody CreatePaymentRequest request) {
        var cmd = new CreatePaymentCommand(
                request.externalReference(),
                request.provider(),
                request.amount(),
                request.currency()
        );
        var result = useCase.create(cmd);
        return new CreatePaymentResponse(result.paymentId(), result.status());
    }
}
