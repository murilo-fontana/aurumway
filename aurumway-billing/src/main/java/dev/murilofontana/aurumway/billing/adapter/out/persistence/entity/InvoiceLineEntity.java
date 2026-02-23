package dev.murilofontana.aurumway.billing.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_lines")
public class InvoiceLineEntity {

    @Id
    @Column(name = "line_id", nullable = false, updatable = false)
    private String lineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal taxRate;

    @Column(name = "line_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal lineTotal;

    protected InvoiceLineEntity() {}

    public InvoiceLineEntity(String lineId, String description, int quantity,
                             BigDecimal unitPrice, BigDecimal taxRate, BigDecimal lineTotal) {
        this.lineId = lineId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
        this.lineTotal = lineTotal;
    }

    public void setInvoice(InvoiceEntity invoice) { this.invoice = invoice; }

    public String getLineId() { return lineId; }
    public InvoiceEntity getInvoice() { return invoice; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTaxRate() { return taxRate; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
