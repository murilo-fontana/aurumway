package dev.murilofontana.aurumway.contracts.adapter.out.persistence;

import dev.murilofontana.aurumway.contracts.adapter.out.persistence.mapper.ContractPersistenceMapper;
import dev.murilofontana.aurumway.contracts.adapter.out.persistence.repository.JpaContractRepository;
import dev.murilofontana.aurumway.contracts.application.port.out.ContractRepositoryPort;
import dev.murilofontana.aurumway.contracts.config.TenantFilterSupport;
import dev.murilofontana.aurumway.contracts.domain.model.Contract;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractId;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ContractRepositoryAdapter implements ContractRepositoryPort {

    private final JpaContractRepository jpa;
    private final TenantFilterSupport tenantFilter;

    public ContractRepositoryAdapter(JpaContractRepository jpa, TenantFilterSupport tenantFilter) {
        this.jpa = jpa;
        this.tenantFilter = tenantFilter;
    }

    @Override
    public Contract save(Contract contract) {
        var entity = ContractPersistenceMapper.toEntity(contract);
        var saved = jpa.save(entity);
        return ContractPersistenceMapper.toDomain(saved);
    }

    @Override
    public Contract update(Contract contract) {
        tenantFilter.enableTenantFilter();
        var entity = jpa.findByContractId(contract.id().value())
                .orElseThrow(() -> new IllegalStateException("Contract not found: " + contract.id().value()));
        ContractPersistenceMapper.updateEntity(entity, contract);
        var saved = jpa.save(entity);
        return ContractPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Contract> findById(ContractId id) {
        tenantFilter.enableTenantFilter();
        return jpa.findByContractId(id.value()).map(ContractPersistenceMapper::toDomain);
    }

    @Override
    public List<Contract> findAll() {
        tenantFilter.enableTenantFilter();
        return jpa.findAllByOrderByCreatedAtDesc().stream()
                .map(ContractPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Contract> findByCustomerId(String customerId) {
        tenantFilter.enableTenantFilter();
        return jpa.findByCustomerId(customerId).stream()
                .map(ContractPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<Contract> findDueForBilling(LocalDate asOf) {
        tenantFilter.enableTenantFilter();
        return jpa.findDueForBilling(asOf).stream()
                .map(ContractPersistenceMapper::toDomain).toList();
    }
}
