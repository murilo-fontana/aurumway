package dev.murilofontana.aurumway.contracts.application.usecase.query;

import dev.murilofontana.aurumway.contracts.application.port.in.GetContractUseCase;
import dev.murilofontana.aurumway.contracts.application.port.out.ContractRepositoryPort;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetContractHandler implements GetContractUseCase {

    private final ContractRepositoryPort repository;

    public GetContractHandler(ContractRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public GetContractResult execute(String contractId) {
        var contract = repository.findById(new ContractId(contractId))
                .orElseThrow(() -> new ContractNotFoundException(contractId));

        var items = contract.items().stream()
                .map(i -> new ItemDetail(i.id().value(), i.description(), i.quantity(),
                        i.unitPrice(), i.taxRate(), i.lineTotal()))
                .toList();

        return new GetContractResult(
                contract.id().value(), contract.customerId(), contract.customerName(),
                contract.currency(), contract.billingCycle().name(),
                contract.startDate(), contract.endDate(),
                contract.status().name(), contract.nextBillingDate(),
                contract.totalPerCycle(), contract.createdAt(), items
        );
    }
}
