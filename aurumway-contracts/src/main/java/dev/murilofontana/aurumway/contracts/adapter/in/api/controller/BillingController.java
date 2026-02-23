package dev.murilofontana.aurumway.contracts.adapter.in.api.controller;

import dev.murilofontana.aurumway.contracts.adapter.in.api.dto.GenerateInvoicesResponse;
import dev.murilofontana.aurumway.contracts.application.port.in.GenerateInvoicesUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/billing")
public class BillingController {

    private final GenerateInvoicesUseCase generateUseCase;

    public BillingController(GenerateInvoicesUseCase generateUseCase) {
        this.generateUseCase = generateUseCase;
    }

    @PostMapping("/generate-invoices")
    public GenerateInvoicesResponse generateInvoices() {
        var result = generateUseCase.execute();
        var invoices = result.invoices().stream()
                .map(i -> new GenerateInvoicesResponse.GeneratedInvoiceDto(
                        i.contractId(), i.invoiceId(), i.totalAmount(), i.currency()))
                .toList();
        return new GenerateInvoicesResponse(result.contractsProcessed(),
                result.invoicesGenerated(), invoices);
    }
}
