package dev.murilofontana.aurumway.billing.application.port.in;

import java.time.LocalDate;

public interface MarkOverdueInvoicesUseCase {

    int execute(LocalDate asOf);
}
