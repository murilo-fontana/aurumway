package dev.murilofontana.aurumway.billing.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.BankStatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaBankStatementRepository extends JpaRepository<BankStatementEntity, String> {

    Optional<BankStatementEntity> findByStatementId(String statementId);

    List<BankStatementEntity> findAllByOrderByImportedAtDesc();
}
