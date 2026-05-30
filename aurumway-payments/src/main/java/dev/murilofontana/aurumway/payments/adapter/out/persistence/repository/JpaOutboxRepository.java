package dev.murilofontana.aurumway.payments.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.payments.adapter.out.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaOutboxRepository extends JpaRepository<OutboxEventEntity, String> {

    List<OutboxEventEntity> findTop100ByStatusOrderByCreatedAtAsc(String status);
}
