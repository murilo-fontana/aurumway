package dev.murilofontana.aurumway.payments.adapter.in.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.murilofontana.aurumway.payments.adapter.in.api.dto.CreatePaymentIntentRequest;
import dev.murilofontana.aurumway.payments.adapter.in.api.dto.CreatePaymentIntentResponse;
import dev.murilofontana.aurumway.payments.application.port.in.CreatePaymentIntentUseCase;
import dev.murilofontana.aurumway.payments.application.port.out.IdempotencyPort;
import dev.murilofontana.aurumway.payments.application.usecase.command.CreatePaymentIntentCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-intents")
public class CreatePaymentIntentController {

    private static final String ENDPOINT = "POST /payment-intents";

    private final CreatePaymentIntentUseCase useCase;
    private final IdempotencyPort idempotency;
    private final ObjectMapper objectMapper;

    public CreatePaymentIntentController(CreatePaymentIntentUseCase useCase,
                                         IdempotencyPort idempotency,
                                         ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.idempotency = idempotency;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreatePaymentIntentRequest request) throws JsonProcessingException {

        var cached = idempotency.find(idempotencyKey, ENDPOINT);
        if (cached.isPresent()) {
            return ResponseEntity.status(cached.get().status()).body(cached.get().body());
        }

        var cmd = new CreatePaymentIntentCommand(
                request.externalReference(),
                request.amount(),
                request.currency(),
                idempotencyKey
        );
        var result = useCase.execute(cmd);
        var response = new CreatePaymentIntentResponse(result.paymentId(), result.clientSecret(), result.status());
        var responseBody = objectMapper.writeValueAsString(response);

        idempotency.store(idempotencyKey, ENDPOINT, HttpStatus.CREATED.value(), responseBody);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }
}
