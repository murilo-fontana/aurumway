package dev.murilofontana.aurumway.payments.adapter.in.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.murilofontana.aurumway.payments.adapter.in.api.dto.RefundPaymentRequest;
import dev.murilofontana.aurumway.payments.adapter.in.api.dto.RefundPaymentResponse;
import dev.murilofontana.aurumway.payments.application.port.in.RefundPaymentUseCase;
import dev.murilofontana.aurumway.payments.application.port.out.IdempotencyPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RefundPaymentController {

    private static final String ENDPOINT = "POST /payments/{paymentId}/refund";

    private final RefundPaymentUseCase useCase;
    private final IdempotencyPort idempotency;
    private final ObjectMapper objectMapper;

    public RefundPaymentController(RefundPaymentUseCase useCase,
                                   IdempotencyPort idempotency,
                                   ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.idempotency = idempotency;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/payments/{paymentId}/refund")
    public ResponseEntity<String> refund(
            @PathVariable String paymentId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody(required = false) RefundPaymentRequest request) throws JsonProcessingException {

        var cached = idempotency.find(idempotencyKey, ENDPOINT);
        if (cached.isPresent()) {
            return ResponseEntity.status(cached.get().status()).body(cached.get().body());
        }

        var amount = request != null ? request.amount() : null;
        var result = useCase.execute(paymentId, amount, idempotencyKey);
        var response = new RefundPaymentResponse(result.paymentId(), result.status(), result.refundedAmount(),
                result.refundableBalance(), result.stripeRefundId());
        var responseBody = objectMapper.writeValueAsString(response);

        idempotency.store(idempotencyKey, ENDPOINT, HttpStatus.OK.value(), responseBody);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
