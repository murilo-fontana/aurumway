package dev.murilofontana.aurumway.payments.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.payments.adapter.out.persistence.entity.IdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaIdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, String> {

    Optional<IdempotencyKeyEntity> findByIdempotencyKey(String idempotencyKey);
}
