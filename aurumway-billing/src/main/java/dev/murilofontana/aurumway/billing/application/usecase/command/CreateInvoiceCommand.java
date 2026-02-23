package dev.murilofontana.aurumway.billing.application.usecase.command;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record CreateInvoiceCommand(
        String customerId,
        String currency,
        List<LineItem> lines
) {
    public CreateInvoiceCommand {
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(currency, "currency is required");
        Objects.requireNonNull(lines, "lines are required");
        if (lines.isEmpty()) throw new IllegalArgumentException("at least one line is required");
    }

    public record LineItem(String description, int quantity, BigDecimal unitPrice, BigDecimal taxRate) {
        public LineItem {
            Objects.requireNonNull(description, "description is required");
            Objects.requireNonNull(unitPrice, "unitPrice is required");
            Objects.requireNonNull(taxRate, "taxRate is required");
        }
    }
}
