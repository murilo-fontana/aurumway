package dev.murilofontana.aurumway.payments.adapter.in.api.controller;

import dev.murilofontana.aurumway.payments.adapter.in.api.dto.GetPaymentResponse;
import dev.murilofontana.aurumway.payments.application.port.in.GetPaymentUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class GetPaymentController {

    private final GetPaymentUseCase useCase;

    public GetPaymentController(GetPaymentUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/{paymentId}")
    public GetPaymentResponse get(@PathVariable String paymentId) {
        var result = useCase.execute(paymentId);
        return new GetPaymentResponse(
                result.paymentId(),
                result.externalReference(),
                result.provider(),
                result.amount(),
                result.currency(),
                result.status(),
                result.refundedAmount(),
                result.createdAt()
        );
    }
}
