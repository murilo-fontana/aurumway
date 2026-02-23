package dev.murilofontana.aurumway.billing.adapter.in.api.controller;

import dev.murilofontana.aurumway.billing.adapter.in.api.dto.*;
import dev.murilofontana.aurumway.billing.application.port.in.*;
import dev.murilofontana.aurumway.billing.application.usecase.command.CreateInvoiceCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final CreateInvoiceUseCase createUseCase;
    private final GetInvoiceUseCase getUseCase;
    private final ListInvoicesUseCase listUseCase;
    private final IssueInvoiceUseCase issueUseCase;
    private final SendInvoiceUseCase sendUseCase;
    private final PayInvoiceUseCase payUseCase;
    private final CancelInvoiceUseCase cancelUseCase;
    private final RefundInvoiceUseCase refundUseCase;

    public InvoiceController(CreateInvoiceUseCase createUseCase, GetInvoiceUseCase getUseCase,
                             ListInvoicesUseCase listUseCase, IssueInvoiceUseCase issueUseCase,
                             SendInvoiceUseCase sendUseCase, PayInvoiceUseCase payUseCase,
                             CancelInvoiceUseCase cancelUseCase, RefundInvoiceUseCase refundUseCase) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
        this.issueUseCase = issueUseCase;
        this.sendUseCase = sendUseCase;
        this.payUseCase = payUseCase;
        this.cancelUseCase = cancelUseCase;
        this.refundUseCase = refundUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateInvoiceResponse create(@Valid @RequestBody CreateInvoiceRequest request) {
        var lines = request.lines().stream()
                .map(l -> new CreateInvoiceCommand.LineItem(l.description(), l.quantity(), l.unitPrice(), l.taxRate()))
                .toList();
        var cmd = new CreateInvoiceCommand(request.customerId(), request.currency(), lines);
        var result = createUseCase.execute(cmd);
        return new CreateInvoiceResponse(result.invoiceId(), result.status(), result.totalAmount(), result.currency());
    }

    @GetMapping("/{invoiceId}")
    public GetInvoiceResponse get(@PathVariable String invoiceId) {
        var result = getUseCase.execute(invoiceId);
        var lines = result.lines().stream()
                .map(l -> new GetInvoiceResponse.LineResponse(l.lineId(), l.description(), l.quantity(),
                        l.unitPrice(), l.taxRate(), l.lineTotal()))
                .toList();
        return new GetInvoiceResponse(result.invoiceId(), result.customerId(), result.invoiceNumber(),
                result.currency(), result.totalAmount(), result.status(), result.refundedAmount(),
                result.issueDate(), result.dueDate(), result.createdAt(), lines);
    }

    @GetMapping
    public List<InvoiceSummaryResponse> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        return listUseCase.execute(status, customerId, from, to).stream()
                .map(s -> new InvoiceSummaryResponse(s.invoiceId(), s.customerId(), s.invoiceNumber(),
                        s.currency(), s.totalAmount(), s.status(), s.dueDate(), s.createdAt()))
                .toList();
    }

    @PostMapping("/{invoiceId}/issue")
    public InvoiceStatusResponse issue(@PathVariable String invoiceId,
                                       @Valid @RequestBody IssueInvoiceRequest request) {
        var result = issueUseCase.execute(invoiceId, request.dueDate());
        return new InvoiceStatusResponse(result.invoiceId(), result.invoiceNumber(), result.status());
    }

    @PostMapping("/{invoiceId}/send")
    public InvoiceStatusResponse send(@PathVariable String invoiceId) {
        var result = sendUseCase.execute(invoiceId);
        return new InvoiceStatusResponse(result.invoiceId(), null, result.status());
    }

    @PostMapping("/{invoiceId}/pay")
    public InvoiceStatusResponse pay(@PathVariable String invoiceId) {
        var result = payUseCase.execute(invoiceId);
        return new InvoiceStatusResponse(result.invoiceId(), null, result.status());
    }

    @PostMapping("/{invoiceId}/cancel")
    public InvoiceStatusResponse cancel(@PathVariable String invoiceId) {
        var result = cancelUseCase.execute(invoiceId);
        return new InvoiceStatusResponse(result.invoiceId(), null, result.status());
    }

    @PostMapping("/{invoiceId}/refund")
    public RefundInvoiceResponse refund(@PathVariable String invoiceId,
                                        @RequestBody(required = false) RefundInvoiceRequest request) {
        var amount = request != null ? request.amount() : null;
        var reason = request != null ? request.reason() : null;
        var result = refundUseCase.execute(invoiceId, amount, reason);
        return new RefundInvoiceResponse(result.invoiceId(), result.status(), result.refundedAmount(),
                result.refundableBalance(), result.ledgerEntryId());
    }
}
