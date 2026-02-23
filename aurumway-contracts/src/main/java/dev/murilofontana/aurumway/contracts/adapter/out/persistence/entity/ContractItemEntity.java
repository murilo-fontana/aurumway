package dev.murilofontana.aurumway.contracts.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "contract_items")
public class ContractItemEntity {

    @Id
    @Column(name = "item_id", nullable = false, updatable = false)
    private String itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private ContractEntity contract;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal taxRate;

    protected ContractItemEntity() {}

    public ContractItemEntity(String itemId, String description, int quantity,
                              BigDecimal unitPrice, BigDecimal taxRate) {
        this.itemId = itemId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
    }

    public void setContract(ContractEntity contract) { this.contract = contract; }

    public String getItemId() { return itemId; }
    public ContractEntity getContract() { return contract; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTaxRate() { return taxRate; }
}
