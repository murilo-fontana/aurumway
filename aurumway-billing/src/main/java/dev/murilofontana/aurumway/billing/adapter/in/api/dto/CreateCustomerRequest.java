package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        String taxId
) {}
