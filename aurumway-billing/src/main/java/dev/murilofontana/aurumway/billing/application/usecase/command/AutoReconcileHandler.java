package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.AutoReconcileUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.LedgerRepositoryPort;
import dev.murilofontana.aurumway.billing.application.port.out.StatementRepositoryPort;
import dev.murilofontana.aurumway.billing.application.usecase.query.StatementNotFoundException;
import dev.murilofontana.aurumway.billing.domain.model.Invoice;
import dev.murilofontana.aurumway.billing.domain.service.LedgerService;
import dev.murilofontana.aurumway.billing.domain.service.ReconciliationEngine;
import dev.murilofontana.aurumway.billing.domain.valueobject.StatementId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AutoReconcileHandler implements AutoReconcileUseCase {

    private final StatementRepositoryPort statementRepo;
    private final InvoiceRepositoryPort invoiceRepo;
    private final LedgerRepositoryPort ledgerRepo;

    public AutoReconcileHandler(StatementRepositoryPort statementRepo,
                                InvoiceRepositoryPort invoiceRepo,
                                LedgerRepositoryPort ledgerRepo) {
        this.statementRepo = statementRepo;
        this.invoiceRepo = invoiceRepo;
        this.ledgerRepo = ledgerRepo;
    }

    @Override
    @Transactional
    public ReconcileResult execute(String statementId) {
        var statement = statementRepo.findById(new StatementId(statementId))
                .orElseThrow(() -> new StatementNotFoundException(statementId));

        var openInvoices = new ArrayList<Invoice>();
        openInvoices.addAll(invoiceRepo.findWithFilters("ISSUED", null, null, null));
        openInvoices.addAll(invoiceRepo.findWithFilters("SENT", null, null, null));
        openInvoices.addAll(invoiceRepo.findWithFilters("OVERDUE", null, null, null));

        var result = ReconciliationEngine.reconcile(statement, openInvoices);

        for (var pair : result.matched()) {
            var invoice = pair.invoice();
            invoice.markPaid();
            invoiceRepo.save(invoice);

            var entryNumber = ledgerRepo.nextEntryNumber();
            var journalEntry = LedgerService.forPaymentReceived(entryNumber, invoice);
            ledgerRepo.save(journalEntry);
        }

        statementRepo.update(statement);

        long unmatchedCount = statement.transactions().stream()
                .filter(tx -> tx.isUnmatched())
                .count();

        var matches = result.matched().stream()
                .map(p -> new MatchDetail(
                        p.transaction().id().value(),
                        p.invoice().id().value(),
                        p.invoice().invoiceNumber()))
                .toList();

        return new ReconcileResult(statementId, statement.status().name(),
                result.matchedCount(), (int) unmatchedCount, matches);
    }
}
