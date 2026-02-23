package dev.murilofontana.aurumway.contracts.application.usecase.command;

import dev.murilofontana.aurumway.contracts.application.port.in.SuspendContractUseCase;
import dev.murilofontana.aurumway.contracts.application.port.out.ContractRepositoryPort;
import dev.murilofontana.aurumway.contracts.application.usecase.query.ContractNotFoundException;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuspendContractHandler implements SuspendContractUseCase {

    private final ContractRepositoryPort repository;

    public SuspendContractHandler(ContractRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ContractStatusResult execute(String contractId) {
        var contract = repository.findById(new ContractId(contractId))
                .orElseThrow(() -> new ContractNotFoundException(contractId));
        contract.suspend();
        repository.update(contract);
        return new ContractStatusResult(contract.id().value(), contract.status().name());
    }
}
