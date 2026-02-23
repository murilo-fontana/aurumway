package dev.murilofontana.aurumway.billing.application.port.out;

import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepositoryPort {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(InvoiceId id);

    List<Invoice> findWithFilters(String status, String customerId, Instant from, Instant to);

    String nextInvoiceNumber(int year);
}
