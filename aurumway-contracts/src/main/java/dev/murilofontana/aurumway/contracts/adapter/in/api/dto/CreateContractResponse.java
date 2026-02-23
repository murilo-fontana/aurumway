package dev.murilofontana.aurumway.contracts.adapter.in.api.dto;

import java.math.BigDecimal;

public record CreateContractResponse(String contractId, String status,
                                     BigDecimal totalPerCycle, String currency) {}
