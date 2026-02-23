package dev.murilofontana.aurumway.billing.adapter.out.persistence.mapper;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.InvoiceEntity;
import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.InvoiceLineEntity;
import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.model.InvoiceLine;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceLineId;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceStatus;

import java.util.ArrayList;

public final class InvoicePersistenceMapper {

    private InvoicePersistenceMapper() {}

    public static InvoiceEntity toEntity(Invoice invoice) {
        var lineEntities = new ArrayList<InvoiceLineEntity>();
        for (var line : invoice.lines()) {
            lineEntities.add(new InvoiceLineEntity(
                    line.id().value(),
                    line.description(),
                    line.quantity(),
                    line.unitPrice(),
                    line.taxRate(),
                    line.lineTotal()
            ));
        }

        return new InvoiceEntity(
                invoice.id().value(),
                invoice.customerId().value(),
                invoice.invoiceNumber(),
                invoice.currency(),
                invoice.totalAmount().amount(),
                invoice.status().name(),
                invoice.issueDate(),
                invoice.dueDate(),
                invoice.refundedAmount(),
                invoice.createdAt(),
                lineEntities
        );
    }

    public static Invoice toDomain(InvoiceEntity entity) {
        var lines = entity.getLines().stream()
                .map(InvoicePersistenceMapper::lineToDomain)
                .toList();

        return Invoice.rehydrate(
                new InvoiceId(entity.getInvoiceId()),
                new CustomerId(entity.getCustomerId()),
                entity.getCurrency(),
                lines,
                InvoiceStatus.valueOf(entity.getStatus()),
                entity.getInvoiceNumber(),
                entity.getIssueDate(),
                entity.getDueDate(),
                entity.getRefundedAmount(),
                entity.getCreatedAt()
        );
    }

    private static InvoiceLine lineToDomain(InvoiceLineEntity entity) {
        return InvoiceLine.rehydrate(
                new InvoiceLineId(entity.getLineId()),
                entity.getDescription(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getTaxRate(),
                entity.getLineTotal()
        );
    }
}
