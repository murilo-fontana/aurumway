package dev.murilofontana.aurumway.contracts.adapter.in.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractSummaryResponse(
        String contractId, String customerId, String customerName,
        String currency, String billingCycle, String status,
        LocalDate startDate, LocalDate endDate, BigDecimal totalPerCycle
) {}
