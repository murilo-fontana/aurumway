package dev.murilofontana.aurumway.billing.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, String> {

    Optional<CustomerEntity> findByCustomerId(String customerId);

    boolean existsByCustomerId(String customerId);
}
