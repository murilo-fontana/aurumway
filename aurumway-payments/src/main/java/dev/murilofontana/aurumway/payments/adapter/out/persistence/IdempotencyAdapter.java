package dev.murilofontana.aurumway.payments.adapter.out.persistence;

import dev.murilofontana.aurumway.payments.adapter.out.persistence.entity.IdempotencyKeyEntity;
import dev.murilofontana.aurumway.payments.adapter.out.persistence.repository.JpaIdempotencyKeyRepository;
import dev.murilofontana.aurumway.payments.application.port.out.IdempotencyPort;
import dev.murilofontana.aurumway.payments.config.TenantFilterSupport;
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
    public Optional<CachedResponse> find(String idempotencyKey, String endpoint) {
        tenantFilter.enableTenantFilter();
        return repository.findByIdempotencyKey(idempotencyKey)
                .filter(entity -> entity.getEndpoint().equals(endpoint))
                .map(entity -> new CachedResponse(entity.getResponseStatus(), entity.getResponseBody()));
    }

    @Override
    public void store(String idempotencyKey, String endpoint, int responseStatus, String responseBody) {
        var entity = new IdempotencyKeyEntity(idempotencyKey, endpoint, responseStatus, responseBody, Instant.now());
        repository.save(entity);
    }
}
