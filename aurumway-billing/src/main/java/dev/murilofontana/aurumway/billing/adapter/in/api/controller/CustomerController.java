package dev.murilofontana.aurumway.billing.adapter.in.api.controller;

import dev.murilofontana.aurumway.billing.adapter.in.api.dto.CreateCustomerRequest;
import dev.murilofontana.aurumway.billing.adapter.in.api.dto.CreateCustomerResponse;
import dev.murilofontana.aurumway.billing.adapter.in.api.dto.GetCustomerResponse;
import dev.murilofontana.aurumway.billing.application.port.in.CreateCustomerUseCase;
import dev.murilofontana.aurumway.billing.application.port.in.GetCustomerUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CreateCustomerUseCase createUseCase;
    private final GetCustomerUseCase getUseCase;

    public CustomerController(CreateCustomerUseCase createUseCase, GetCustomerUseCase getUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCustomerResponse create(@Valid @RequestBody CreateCustomerRequest request) {
        var result = createUseCase.execute(request.name(), request.email(), request.taxId());
        return new CreateCustomerResponse(result.customerId(), result.name(), result.email());
    }

    @GetMapping("/{customerId}")
    public GetCustomerResponse get(@PathVariable String customerId) {
        var result = getUseCase.execute(customerId);
        return new GetCustomerResponse(result.customerId(), result.name(), result.email(),
                result.taxId(), result.createdAt());
    }
}
