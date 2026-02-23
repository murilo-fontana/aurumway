package dev.murilofontana.aurumway.contracts.application.port.out;

import java.math.BigDecimal;
import java.util.List;

public interface BillingServicePort {

    CreateInvoiceResponse createInvoice(CreateInvoiceRequest request);

    record CreateInvoiceRequest(String customerId, String currency, List<LineItem> lines) {}

    record LineItem(String description, int quantity, BigDecimal unitPrice, BigDecimal taxRate) {}

    record CreateInvoiceResponse(String invoiceId, String status, BigDecimal totalAmount, String currency) {}
}
