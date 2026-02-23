package dev.murilofontana.aurumway.payments.adapter.in.api.controller;

import dev.murilofontana.aurumway.payments.adapter.in.api.dto.RefundPaymentRequest;
import dev.murilofontana.aurumway.payments.adapter.in.api.dto.RefundPaymentResponse;
import dev.murilofontana.aurumway.payments.application.port.in.RefundPaymentUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
public class RefundPaymentController {

    private final RefundPaymentUseCase useCase;

    public RefundPaymentController(RefundPaymentUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/payments/{paymentId}/refund")
    public RefundPaymentResponse refund(
            @PathVariable String paymentId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody(required = false) RefundPaymentRequest request) {
        var amount = request != null ? request.amount() : null;
        var result = useCase.execute(paymentId, amount, idempotencyKey);
        return new RefundPaymentResponse(result.paymentId(), result.status(), result.refundedAmount(),
                result.refundableBalance(), result.stripeRefundId());
    }
}
