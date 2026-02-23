package dev.murilofontana.aurumway.billing.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface ListInvoicesUseCase {

    List<InvoiceSummary> execute(String status, String customerId, Instant from, Instant to);

    record InvoiceSummary(
            String invoiceId, String customerId, String invoiceNumber, String currency,
            BigDecimal totalAmount, String status, LocalDate dueDate, Instant createdAt
    ) {}
}
