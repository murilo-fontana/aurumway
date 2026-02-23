package dev.murilofontana.aurumway.contracts.adapter.in.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record GetContractResponse(
        String contractId, String customerId, String customerName,
        String currency, String billingCycle,
        LocalDate startDate, LocalDate endDate,
        String status, LocalDate nextBillingDate,
        BigDecimal totalPerCycle, Instant createdAt,
        List<ItemDto> items
) {
    public record ItemDto(String itemId, String description, int quantity,
                          BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineTotal) {}
}
