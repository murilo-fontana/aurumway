package dev.murilofontana.aurumway.contracts.application.usecase.query;

import dev.murilofontana.aurumway.contracts.application.port.in.ListContractsUseCase;
import dev.murilofontana.aurumway.contracts.application.port.out.ContractRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListContractsHandler implements ListContractsUseCase {

    private final ContractRepositoryPort repository;

    public ListContractsHandler(ContractRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractSummary> execute(String customerId) {
        var contracts = customerId != null
                ? repository.findByCustomerId(customerId)
                : repository.findAll();

        return contracts.stream()
                .map(c -> new ContractSummary(
                        c.id().value(), c.customerId(), c.customerName(),
                        c.currency(), c.billingCycle().name(), c.status().name(),
                        c.startDate(), c.endDate(), c.totalPerCycle()))
                .toList();
    }
}
