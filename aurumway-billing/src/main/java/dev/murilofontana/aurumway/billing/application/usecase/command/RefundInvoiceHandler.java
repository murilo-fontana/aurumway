package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.RefundInvoiceUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.LedgerRepositoryPort;
import dev.murilofontana.aurumway.billing.application.usecase.query.InvoiceNotFoundException;
import dev.murilofontana.aurumway.billing.domain.service.LedgerService;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RefundInvoiceHandler implements RefundInvoiceUseCase {

    private final InvoiceRepositoryPort repository;
    private final LedgerRepositoryPort ledger;

    public RefundInvoiceHandler(InvoiceRepositoryPort repository, LedgerRepositoryPort ledger) {
        this.repository = repository;
        this.ledger = ledger;
    }

    @Override
    @Transactional
    public RefundInvoiceResult execute(String invoiceId, BigDecimal amount, String reason) {
        var invoice = repository.findById(new InvoiceId(invoiceId))
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        var refundAmount = amount != null ? amount : invoice.refundableBalance();

        invoice.refund(refundAmount);
        repository.save(invoice);

        var entryNumber = ledger.nextEntryNumber();
        var journalEntry = LedgerService.forRefundIssued(entryNumber, invoice, refundAmount);
        ledger.save(journalEntry);

        return new RefundInvoiceResult(
                invoice.id().value(),
                invoice.status().name(),
                invoice.refundedAmount(),
                invoice.refundableBalance(),
                journalEntry.id().value()
        );
    }
}
