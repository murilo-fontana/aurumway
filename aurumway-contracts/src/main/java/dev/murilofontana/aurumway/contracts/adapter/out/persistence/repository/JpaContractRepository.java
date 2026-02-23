package dev.murilofontana.aurumway.contracts.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.contracts.adapter.out.persistence.entity.ContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JpaContractRepository extends JpaRepository<ContractEntity, String> {

    Optional<ContractEntity> findByContractId(String contractId);

    List<ContractEntity> findByCustomerId(String customerId);

    List<ContractEntity> findByStatus(String status);

    @Query("SELECT c FROM ContractEntity c WHERE c.status = 'ACTIVE' " +
           "AND c.nextBillingDate IS NOT NULL AND c.nextBillingDate <= :asOf " +
           "AND c.endDate >= :asOf")
    List<ContractEntity> findDueForBilling(LocalDate asOf);

    List<ContractEntity> findAllByOrderByCreatedAtDesc();
}
