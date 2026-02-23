package dev.murilofontana.aurumway.billing.adapter.out.persistence;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.mapper.CustomerPersistenceMapper;
import dev.murilofontana.aurumway.billing.adapter.out.persistence.repository.JpaCustomerRepository;
import dev.murilofontana.aurumway.billing.application.port.out.CustomerRepositoryPort;
import dev.murilofontana.aurumway.billing.config.TenantFilterSupport;
import dev.murilofontana.aurumway.billing.domain.model.Customer;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

    private final JpaCustomerRepository jpa;
    private final TenantFilterSupport tenantFilter;

    public CustomerRepositoryAdapter(JpaCustomerRepository jpa, TenantFilterSupport tenantFilter) {
        this.jpa = jpa;
        this.tenantFilter = tenantFilter;
    }

    @Override
    public Customer save(Customer customer) {
        var entity = CustomerPersistenceMapper.toEntity(customer);
        var saved = jpa.save(entity);
        return CustomerPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        tenantFilter.enableTenantFilter();
        return jpa.findByCustomerId(id.value()).map(CustomerPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(CustomerId id) {
        tenantFilter.enableTenantFilter();
        return jpa.existsByCustomerId(id.value());
    }
}
