package dev.murilofontana.aurumway.contracts.adapter.in.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateContractRequest(
        @NotBlank String customerId,
        @NotBlank String customerName,
        @NotBlank String currency,
        @NotBlank String billingCycle,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotEmpty @Valid List<ItemRequest> items
) {
    public record ItemRequest(
            @NotBlank String description,
            @Positive int quantity,
            @NotNull BigDecimal unitPrice,
            @NotNull BigDecimal taxRate
    ) {}
}
