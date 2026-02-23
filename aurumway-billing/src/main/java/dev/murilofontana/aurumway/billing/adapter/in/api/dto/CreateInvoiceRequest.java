package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateInvoiceRequest(
        @NotBlank String customerId,
        @NotBlank String currency,
        @NotEmpty @Valid List<LineItemRequest> lines
) {
    public record LineItemRequest(
            @NotBlank String description,
            @Min(1) int quantity,
            @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
            @NotNull @DecimalMin("0.00") BigDecimal taxRate
    ) {}
}
