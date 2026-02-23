package dev.murilofontana.aurumway.contracts.adapter.in.api.controller;

import dev.murilofontana.aurumway.contracts.adapter.in.api.dto.*;
import dev.murilofontana.aurumway.contracts.application.port.in.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    private final CreateContractUseCase createUseCase;
    private final GetContractUseCase getUseCase;
    private final ListContractsUseCase listUseCase;
    private final ActivateContractUseCase activateUseCase;
    private final SuspendContractUseCase suspendUseCase;
    private final ResumeContractUseCase resumeUseCase;
    private final TerminateContractUseCase terminateUseCase;

    public ContractController(CreateContractUseCase createUseCase,
                              GetContractUseCase getUseCase,
                              ListContractsUseCase listUseCase,
                              ActivateContractUseCase activateUseCase,
                              SuspendContractUseCase suspendUseCase,
                              ResumeContractUseCase resumeUseCase,
                              TerminateContractUseCase terminateUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.resumeUseCase = resumeUseCase;
        this.terminateUseCase = terminateUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateContractResponse create(@RequestBody @Valid CreateContractRequest request) {
        var command = new CreateContractUseCase.CreateContractCommand(
                request.customerId(), request.customerName(), request.currency(),
                request.billingCycle(), request.startDate(), request.endDate(),
                request.items().stream()
                        .map(i -> new CreateContractUseCase.ItemCommand(
                                i.description(), i.quantity(), i.unitPrice(), i.taxRate()))
                        .toList()
        );
        var result = createUseCase.execute(command);
        return new CreateContractResponse(result.contractId(), result.status(),
                result.totalPerCycle(), result.currency());
    }

    @GetMapping("/{contractId}")
    public GetContractResponse get(@PathVariable String contractId) {
        var result = getUseCase.execute(contractId);
        var items = result.items().stream()
                .map(i -> new GetContractResponse.ItemDto(
                        i.itemId(), i.description(), i.quantity(),
                        i.unitPrice(), i.taxRate(), i.lineTotal()))
                .toList();
        return new GetContractResponse(
                result.contractId(), result.customerId(), result.customerName(),
                result.currency(), result.billingCycle(),
                result.startDate(), result.endDate(),
                result.status(), result.nextBillingDate(),
                result.totalPerCycle(), result.createdAt(), items
        );
    }

    @GetMapping
    public List<ContractSummaryResponse> list(@RequestParam(required = false) String customerId) {
        return listUseCase.execute(customerId).stream()
                .map(c -> new ContractSummaryResponse(
                        c.contractId(), c.customerId(), c.customerName(),
                        c.currency(), c.billingCycle(), c.status(),
                        c.startDate(), c.endDate(), c.totalPerCycle()))
                .toList();
    }

    @PostMapping("/{contractId}/activate")
    public ContractStatusResponse activate(@PathVariable String contractId) {
        var result = activateUseCase.execute(contractId);
        return new ContractStatusResponse(result.contractId(), result.status());
    }

    @PostMapping("/{contractId}/suspend")
    public ContractStatusResponse suspend(@PathVariable String contractId) {
        var result = suspendUseCase.execute(contractId);
        return new ContractStatusResponse(result.contractId(), result.status());
    }

    @PostMapping("/{contractId}/resume")
    public ContractStatusResponse resume(@PathVariable String contractId) {
        var result = resumeUseCase.execute(contractId);
        return new ContractStatusResponse(result.contractId(), result.status());
    }

    @PostMapping("/{contractId}/terminate")
    public ContractStatusResponse terminate(@PathVariable String contractId) {
        var result = terminateUseCase.execute(contractId);
        return new ContractStatusResponse(result.contractId(), result.status());
    }
}
