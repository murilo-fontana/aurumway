package dev.murilofontana.aurumway.contracts.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface GetContractUseCase {

    GetContractResult execute(String contractId);

    record GetContractResult(String contractId, String customerId, String customerName,
                             String currency, String billingCycle,
                             LocalDate startDate, LocalDate endDate,
                             String status, LocalDate nextBillingDate,
                             BigDecimal totalPerCycle, Instant createdAt,
                             List<ItemDetail> items) {}

    record ItemDetail(String itemId, String description, int quantity,
                      BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineTotal) {}
}
