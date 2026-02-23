package dev.murilofontana.aurumway.contracts.application.usecase.command;

import dev.murilofontana.aurumway.contracts.application.port.in.GenerateInvoicesUseCase;
import dev.murilofontana.aurumway.contracts.application.port.out.BillingServicePort;
import dev.murilofontana.aurumway.contracts.application.port.out.ContractRepositoryPort;
import dev.murilofontana.aurumway.contracts.domain.model.Contract;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class GenerateInvoicesHandler implements GenerateInvoicesUseCase {

    private final ContractRepositoryPort contractRepo;
    private final BillingServicePort billingService;

    public GenerateInvoicesHandler(ContractRepositoryPort contractRepo, BillingServicePort billingService) {
        this.contractRepo = contractRepo;
        this.billingService = billingService;
    }

    @Override
    @Transactional
    public GenerateInvoicesResult execute() {
        var today = LocalDate.now();
        var dueContracts = contractRepo.findDueForBilling(today);
        var generated = new ArrayList<GeneratedInvoice>();

        for (var contract : dueContracts) {
            var invoiceResponse = createInvoiceFromContract(contract);

            if (invoiceResponse != null) {
                generated.add(new GeneratedInvoice(
                        contract.id().value(), invoiceResponse.invoiceId(),
                        invoiceResponse.totalAmount(), invoiceResponse.currency()));

                contract.advanceBillingDate();
                contractRepo.update(contract);
            }
        }

        return new GenerateInvoicesResult(dueContracts.size(), generated.size(), generated);
    }

    private BillingServicePort.CreateInvoiceResponse createInvoiceFromContract(Contract contract) {
        var lines = contract.items().stream()
                .map(item -> new BillingServicePort.LineItem(
                        item.description(), item.quantity(), item.unitPrice(), item.taxRate()))
                .toList();

        var request = new BillingServicePort.CreateInvoiceRequest(
                contract.customerId(), contract.currency(), lines);

        return billingService.createInvoice(request);
    }
}
