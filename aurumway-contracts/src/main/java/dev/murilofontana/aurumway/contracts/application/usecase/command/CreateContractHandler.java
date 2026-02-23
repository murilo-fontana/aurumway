package dev.murilofontana.aurumway.contracts.application.usecase.command;

import dev.murilofontana.aurumway.contracts.application.port.in.CreateContractUseCase;
import dev.murilofontana.aurumway.contracts.application.port.out.ContractRepositoryPort;
import dev.murilofontana.aurumway.contracts.domain.model.Contract;
import dev.murilofontana.aurumway.contracts.domain.model.ContractItem;
import dev.murilofontana.aurumway.contracts.domain.valueobject.BillingCycle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateContractHandler implements CreateContractUseCase {

    private final ContractRepositoryPort repository;

    public CreateContractHandler(ContractRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CreateContractResult execute(CreateContractCommand command) {
        var items = command.items().stream()
                .map(i -> ContractItem.create(i.description(), i.quantity(), i.unitPrice(), i.taxRate()))
                .toList();

        var contract = Contract.createDraft(
                command.customerId(), command.customerName(), command.currency(),
                BillingCycle.valueOf(command.billingCycle()),
                command.startDate(), command.endDate(), items
        );

        var saved = repository.save(contract);
        return new CreateContractResult(saved.id().value(), saved.status().name(),
                saved.totalPerCycle(), saved.currency());
    }
}
