package dev.murilofontana.aurumway.contracts.adapter.out.persistence.mapper;

import dev.murilofontana.aurumway.contracts.adapter.out.persistence.entity.ContractEntity;
import dev.murilofontana.aurumway.contracts.adapter.out.persistence.entity.ContractItemEntity;
import dev.murilofontana.aurumway.contracts.domain.model.Contract;
import dev.murilofontana.aurumway.contracts.domain.model.ContractItem;
import dev.murilofontana.aurumway.contracts.domain.valueobject.BillingCycle;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractId;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractItemId;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractStatus;

import java.util.ArrayList;

public final class ContractPersistenceMapper {

    private ContractPersistenceMapper() {}

    public static ContractEntity toEntity(Contract contract) {
        var itemEntities = new ArrayList<ContractItemEntity>();
        for (var item : contract.items()) {
            itemEntities.add(new ContractItemEntity(
                    item.id().value(), item.description(), item.quantity(),
                    item.unitPrice(), item.taxRate()
            ));
        }
        return new ContractEntity(
                contract.id().value(), contract.customerId(), contract.customerName(),
                contract.currency(), contract.billingCycle().name(),
                contract.startDate(), contract.endDate(),
                contract.status().name(), contract.nextBillingDate(),
                contract.createdAt(), itemEntities
        );
    }

    public static Contract toDomain(ContractEntity entity) {
        var items = entity.getItems().stream()
                .map(ContractPersistenceMapper::itemToDomain)
                .toList();

        return Contract.rehydrate(
                new ContractId(entity.getContractId()),
                entity.getCustomerId(), entity.getCustomerName(),
                entity.getCurrency(),
                BillingCycle.valueOf(entity.getBillingCycle()),
                entity.getStartDate(), entity.getEndDate(),
                ContractStatus.valueOf(entity.getStatus()),
                entity.getNextBillingDate(), items, entity.getCreatedAt()
        );
    }

    public static void updateEntity(ContractEntity entity, Contract domain) {
        entity.setStatus(domain.status().name());
        entity.setNextBillingDate(domain.nextBillingDate());
    }

    private static ContractItem itemToDomain(ContractItemEntity e) {
        return ContractItem.rehydrate(
                new ContractItemId(e.getItemId()),
                e.getDescription(), e.getQuantity(), e.getUnitPrice(), e.getTaxRate()
        );
    }
}
