package dev.murilofontana.aurumway.billing.adapter.in.api.controller;

import dev.murilofontana.aurumway.billing.adapter.in.api.dto.GetLedgerEntryResponse;
import dev.murilofontana.aurumway.billing.adapter.in.api.dto.LedgerEntrySummaryResponse;
import dev.murilofontana.aurumway.billing.application.port.in.GetLedgerEntryUseCase;
import dev.murilofontana.aurumway.billing.application.port.in.ListLedgerEntriesUseCase;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    private final GetLedgerEntryUseCase getUseCase;
    private final ListLedgerEntriesUseCase listUseCase;

    public LedgerController(GetLedgerEntryUseCase getUseCase, ListLedgerEntriesUseCase listUseCase) {
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @GetMapping("/{entryId}")
    public GetLedgerEntryResponse get(@PathVariable String entryId) {
        var result = getUseCase.execute(entryId);
        var lines = result.lines().stream()
                .map(l -> new GetLedgerEntryResponse.LedgerLineResponse(
                        l.lineId(), l.accountCode(), l.debit(), l.credit()))
                .toList();
        return new GetLedgerEntryResponse(
                result.entryId(), result.entryNumber(), result.entryType(), result.referenceId(),
                result.currency(), result.description(), result.createdAt(), lines
        );
    }

    @GetMapping
    public List<LedgerEntrySummaryResponse> list(
            @RequestParam(required = false) String entryType,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {
        return listUseCase.execute(entryType, from, to).stream()
                .map(s -> new LedgerEntrySummaryResponse(
                        s.entryId(), s.entryNumber(), s.entryType(), s.referenceId(),
                        s.currency(), s.description(), s.createdAt()))
                .toList();
    }
}
