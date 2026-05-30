package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.MarkOverdueInvoicesUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.InvoiceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class MarkOverdueInvoicesHandler implements MarkOverdueInvoicesUseCase {

    private final InvoiceRepositoryPort repository;

    public MarkOverdueInvoicesHandler(InvoiceRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public int execute(LocalDate asOf) {
        var candidates = repository.findOverdueCandidates(asOf);
        for (var invoice : candidates) {
            invoice.markOverdue();
            repository.save(invoice);
        }
        return candidates.size();
    }
}
