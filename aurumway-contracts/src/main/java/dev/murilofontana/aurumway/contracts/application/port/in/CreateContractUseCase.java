package dev.murilofontana.aurumway.contracts.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CreateContractUseCase {

    CreateContractResult execute(CreateContractCommand command);

    record CreateContractCommand(String customerId, String customerName, String currency,
                                 String billingCycle, LocalDate startDate, LocalDate endDate,
                                 List<ItemCommand> items) {}

    record ItemCommand(String description, int quantity, BigDecimal unitPrice, BigDecimal taxRate) {}

    record CreateContractResult(String contractId, String status, BigDecimal totalPerCycle, String currency) {}
}
