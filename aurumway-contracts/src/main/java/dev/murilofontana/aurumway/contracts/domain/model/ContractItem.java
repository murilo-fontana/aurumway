package dev.murilofontana.aurumway.contracts.domain.model;

import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractItemId;

import java.math.BigDecimal;
import java.util.Objects;

public final class ContractItem {

    private final ContractItemId id;
    private final String description;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal taxRate;

    private ContractItem(ContractItemId id, String description, int quantity,
                         BigDecimal unitPrice, BigDecimal taxRate) {
        this.id = Objects.requireNonNull(id);
        this.description = Objects.requireNonNull(description);
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice);
        this.taxRate = Objects.requireNonNull(taxRate);
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (unitPrice.signum() <= 0) throw new IllegalArgumentException("Unit price must be positive");
        if (taxRate.signum() < 0) throw new IllegalArgumentException("Tax rate must be non-negative");
    }

    public static ContractItem create(String description, int quantity, BigDecimal unitPrice, BigDecimal taxRate) {
        return new ContractItem(ContractItemId.newId(), description, quantity, unitPrice, taxRate);
    }

    public static ContractItem rehydrate(ContractItemId id, String description, int quantity,
                                         BigDecimal unitPrice, BigDecimal taxRate) {
        return new ContractItem(id, description, quantity, unitPrice, taxRate);
    }

    public BigDecimal lineTotal() {
        var subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        var tax = subtotal.multiply(taxRate);
        return subtotal.add(tax);
    }

    public ContractItemId id() { return id; }
    public String description() { return description; }
    public int quantity() { return quantity; }
    public BigDecimal unitPrice() { return unitPrice; }
    public BigDecimal taxRate() { return taxRate; }
}
