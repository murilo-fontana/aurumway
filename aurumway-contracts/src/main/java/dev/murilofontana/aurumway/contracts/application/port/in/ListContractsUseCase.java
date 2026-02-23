package dev.murilofontana.aurumway.contracts.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ListContractsUseCase {

    List<ContractSummary> execute(String customerId);

    record ContractSummary(String contractId, String customerId, String customerName,
                           String currency, String billingCycle, String status,
                           LocalDate startDate, LocalDate endDate,
                           BigDecimal totalPerCycle) {}
}
