package dev.murilofontana.aurumway.billing.adapter.in.api.controller;

import dev.murilofontana.aurumway.billing.adapter.in.api.dto.*;
import dev.murilofontana.aurumway.billing.application.port.in.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/statements")
public class StatementController {

    private final ImportStatementUseCase importUseCase;
    private final AutoReconcileUseCase reconcileUseCase;
    private final ManualMatchUseCase manualMatchUseCase;
    private final GetStatementUseCase getUseCase;
    private final ListStatementsUseCase listUseCase;

    public StatementController(ImportStatementUseCase importUseCase,
                               AutoReconcileUseCase reconcileUseCase,
                               ManualMatchUseCase manualMatchUseCase,
                               GetStatementUseCase getUseCase,
                               ListStatementsUseCase listUseCase) {
        this.importUseCase = importUseCase;
        this.reconcileUseCase = reconcileUseCase;
        this.manualMatchUseCase = manualMatchUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImportStatementResponse importStatement(
            @RequestParam String bankAccount,
            @RequestPart MultipartFile file) throws IOException {
        var result = importUseCase.execute(bankAccount, file.getOriginalFilename(), file.getInputStream());
        return new ImportStatementResponse(
                result.statementId(), result.status(),
                result.transactionCount(), result.importedAt()
        );
    }

    @PostMapping("/{statementId}/reconcile")
    public ReconcileResponse reconcile(@PathVariable String statementId) {
        var result = reconcileUseCase.execute(statementId);
        var matches = result.matches().stream()
                .map(m -> new ReconcileResponse.MatchDetailDto(
                        m.transactionId(), m.invoiceId(), m.invoiceNumber()))
                .toList();
        return new ReconcileResponse(result.statementId(), result.status(),
                result.matchedCount(), result.unmatchedCount(), matches);
    }

    @PostMapping("/{statementId}/transactions/{transactionId}/match")
    public ManualMatchResponse manualMatch(
            @PathVariable String statementId,
            @PathVariable String transactionId,
            @RequestBody @Valid ManualMatchRequest request) {
        var result = manualMatchUseCase.execute(statementId, transactionId, request.invoiceId());
        return new ManualMatchResponse(result.transactionId(), result.invoiceId(), result.status());
    }

    @GetMapping("/{statementId}")
    public GetStatementResponse get(@PathVariable String statementId) {
        var result = getUseCase.execute(statementId);
        var txs = result.transactions().stream()
                .map(tx -> new GetStatementResponse.TransactionDto(
                        tx.transactionId(), tx.transactionDate(), tx.amount(),
                        tx.currency(), tx.description(), tx.reference(),
                        tx.type(), tx.reconciliationStatus(), tx.matchedInvoiceId()))
                .toList();
        return new GetStatementResponse(result.statementId(), result.bankAccount(),
                result.filename(), result.importedAt(), result.status(), txs);
    }

    @GetMapping
    public List<StatementSummaryResponse> list() {
        return listUseCase.execute().stream()
                .map(s -> new StatementSummaryResponse(
                        s.statementId(), s.bankAccount(), s.filename(),
                        s.importedAt(), s.status(), s.transactionCount()))
                .toList();
    }
}
