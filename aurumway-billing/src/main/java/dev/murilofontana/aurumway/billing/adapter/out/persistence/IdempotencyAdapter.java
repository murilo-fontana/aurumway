package dev.murilofontana.aurumway.billing.adapter.out.persistence;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.IdempotencyKeyEntity;
import dev.murilofontana.aurumway.billing.adapter.out.persistence.repository.JpaIdempotencyKeyRepository;
import dev.murilofontana.aurumway.billing.application.port.out.IdempotencyPort;
import dev.murilofontana.aurumway.billing.config.TenantFilterSupport;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class IdempotencyAdapter implements IdempotencyPort {

    private final JpaIdempotencyKeyRepository repository;
    private final TenantFilterSupport tenantFilter;

    public IdempotencyAdapter(JpaIdempotencyKeyRepository repository, TenantFilterSupport tenantFilter) {
        this.repository = repository;
        this.tenantFilter = tenantFilter;
    }

    @Override
    public Optional<String> findResourceId(String idempotencyKey, String endpoint) {
        tenantFilter.enableTenantFilter();
        return repository.findByIdempotencyKey(idempotencyKey)
                .filter(entity -> entity.getEndpoint().equals(endpoint))
                .map(IdempotencyKeyEntity::getResourceId);
    }

    @Override
    public void store(String idempotencyKey, String endpoint, String resourceId) {
        repository.save(new IdempotencyKeyEntity(idempotencyKey, endpoint, resourceId, Instant.now()));
    }
}
