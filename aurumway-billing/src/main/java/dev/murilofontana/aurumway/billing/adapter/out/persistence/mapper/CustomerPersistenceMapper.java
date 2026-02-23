package dev.murilofontana.aurumway.billing.adapter.out.persistence.mapper;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.CustomerEntity;
import dev.murilofontana.aurumway.billing.domain.model.Customer;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;

public final class CustomerPersistenceMapper {

    private CustomerPersistenceMapper() {}

    public static CustomerEntity toEntity(Customer customer) {
        return new CustomerEntity(
                customer.id().value(),
                customer.name(),
                customer.email(),
                customer.taxId(),
                customer.createdAt()
        );
    }

    public static Customer toDomain(CustomerEntity entity) {
        return Customer.rehydrate(
                new CustomerId(entity.getCustomerId()),
                entity.getName(),
                entity.getEmail(),
                entity.getTaxId(),
                entity.getCreatedAt()
        );
    }
}
