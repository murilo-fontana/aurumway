package dev.murilofontana.aurumway.contracts.application.port.out;

import dev.murilofontana.aurumway.contracts.domain.model.Contract;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepositoryPort {

    Contract save(Contract contract);

    Contract update(Contract contract);

    Optional<Contract> findById(ContractId id);

    List<Contract> findAll();

    List<Contract> findByCustomerId(String customerId);

    List<Contract> findDueForBilling(LocalDate asOf);
}
