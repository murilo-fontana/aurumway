package dev.murilofontana.aurumway.billing.adapter.out.persistence;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.mapper.StatementPersistenceMapper;
import dev.murilofontana.aurumway.billing.adapter.out.persistence.repository.JpaBankStatementRepository;
import dev.murilofontana.aurumway.billing.application.port.out.StatementRepositoryPort;
import dev.murilofontana.aurumway.billing.config.TenantFilterSupport;
import dev.murilofontana.aurumway.billing.domain.model.BankStatement;
import dev.murilofontana.aurumway.billing.domain.valueobject.StatementId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StatementRepositoryAdapter implements StatementRepositoryPort {

    private final JpaBankStatementRepository jpa;
    private final TenantFilterSupport tenantFilter;

    public StatementRepositoryAdapter(JpaBankStatementRepository jpa, TenantFilterSupport tenantFilter) {
        this.jpa = jpa;
        this.tenantFilter = tenantFilter;
    }

    @Override
    public BankStatement save(BankStatement statement) {
        var entity = StatementPersistenceMapper.toEntity(statement);
        var saved = jpa.save(entity);
        return StatementPersistenceMapper.toDomain(saved);
    }

    @Override
    public BankStatement update(BankStatement statement) {
        tenantFilter.enableTenantFilter();
        var entity = jpa.findByStatementId(statement.id().value())
                .orElseThrow(() -> new IllegalStateException("Statement not found: " + statement.id().value()));
        StatementPersistenceMapper.updateEntity(entity, statement);
        var saved = jpa.save(entity);
        return StatementPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<BankStatement> findById(StatementId id) {
        tenantFilter.enableTenantFilter();
        return jpa.findByStatementId(id.value()).map(StatementPersistenceMapper::toDomain);
    }

    @Override
    public List<BankStatement> findAll() {
        tenantFilter.enableTenantFilter();
        return jpa.findAllByOrderByImportedAtDesc().stream()
                .map(StatementPersistenceMapper::toDomain)
                .toList();
    }
}
