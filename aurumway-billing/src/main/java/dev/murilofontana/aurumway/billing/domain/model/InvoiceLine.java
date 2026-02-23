package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceLineId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class InvoiceLine {

    private final InvoiceLineId id;
    private final String description;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal taxRate;
    private final BigDecimal lineTotal;

    private InvoiceLine(InvoiceLineId id, String description, int quantity,
                        BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineTotal) {
        this.id = Objects.requireNonNull(id);
        this.description = Objects.requireNonNull(description);
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice);
        this.taxRate = Objects.requireNonNull(taxRate);
        this.lineTotal = Objects.requireNonNull(lineTotal);
    }

    public static InvoiceLine create(String description, int quantity, BigDecimal unitPrice, BigDecimal taxRate) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        if (unitPrice.signum() <= 0) throw new IllegalArgumentException("unitPrice must be > 0");
        if (taxRate.signum() < 0) throw new IllegalArgumentException("taxRate must not be negative");

        var lineTotal = unitPrice
                .multiply(BigDecimal.valueOf(quantity))
                .multiply(BigDecimal.ONE.add(taxRate))
                .setScale(2, RoundingMode.HALF_UP);

        return new InvoiceLine(InvoiceLineId.newId(), description, quantity, unitPrice, taxRate, lineTotal);
    }

    public static InvoiceLine rehydrate(InvoiceLineId id, String description, int quantity,
                                        BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineTotal) {
        return new InvoiceLine(id, description, quantity, unitPrice, taxRate, lineTotal);
    }

    public InvoiceLineId id() { return id; }
    public String description() { return description; }
    public int quantity() { return quantity; }
    public BigDecimal unitPrice() { return unitPrice; }
    public BigDecimal taxRate() { return taxRate; }
    public BigDecimal lineTotal() { return lineTotal; }
}
