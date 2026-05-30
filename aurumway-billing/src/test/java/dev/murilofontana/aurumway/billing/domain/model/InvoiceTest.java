package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;
import dev.murilofontana.aurumway.billing.domain.valueobject.InvoiceStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvoiceTest {

    private static Invoice draftWithSingleLine() {
        var line = InvoiceLine.create("Consulting", 1, new BigDecimal("100.00"), new BigDecimal("0.10"));
        return Invoice.createDraft(new CustomerId("cust-1"), "USD", List.of(line));
    }

    private static Invoice paidInvoice() {
        var invoice = draftWithSingleLine();
        invoice.issue("INV-001", LocalDate.now().plusDays(30));
        invoice.send();
        invoice.markPaid();
        return invoice;
    }

    @Nested
    class Creation {

        @Test
        void createsDraftWithZeroRefundAndDraftStatus() {
            var invoice = draftWithSingleLine();

            assertThat(invoice.status()).isEqualTo(InvoiceStatus.DRAFT);
            assertThat(invoice.refundedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(invoice.invoiceNumber()).isNull();
            assertThat(invoice.id()).isNotNull();
        }

        @Test
        void rejectsEmptyLines() {
            assertThatThrownBy(() -> Invoice.createDraft(new CustomerId("c"), "USD", List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least one line");
        }

        @Test
        void totalAmountSumsLinesWithTax() {
            var invoice = draftWithSingleLine();

            assertThat(invoice.totalAmount().amount()).isEqualByComparingTo("110.00");
            assertThat(invoice.totalAmount().currency()).isEqualTo("USD");
        }
    }

    @Nested
    class Lifecycle {

        @Test
        void issueTransitionsDraftToIssued() {
            var invoice = draftWithSingleLine();
            var due = LocalDate.now().plusDays(30);

            invoice.issue("INV-001", due);

            assertThat(invoice.status()).isEqualTo(InvoiceStatus.ISSUED);
            assertThat(invoice.invoiceNumber()).isEqualTo("INV-001");
            assertThat(invoice.dueDate()).isEqualTo(due);
            assertThat(invoice.issueDate()).isEqualTo(LocalDate.now());
        }

        @Test
        void cannotIssueTwice() {
            var invoice = draftWithSingleLine();
            invoice.issue("INV-001", LocalDate.now().plusDays(30));

            assertThatThrownBy(() -> invoice.issue("INV-002", LocalDate.now().plusDays(30)))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void sendRequiresIssuedStatus() {
            var invoice = draftWithSingleLine();

            assertThatThrownBy(invoice::send).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void fullHappyPathDraftToPaid() {
            var invoice = draftWithSingleLine();

            invoice.issue("INV-001", LocalDate.now().plusDays(30));
            invoice.send();
            invoice.markPaid();

            assertThat(invoice.status()).isEqualTo(InvoiceStatus.PAID);
        }

        @Test
        void markPaidAllowedFromOverdue() {
            var invoice = draftWithSingleLine();
            invoice.issue("INV-001", LocalDate.now().plusDays(30));
            invoice.send();
            invoice.markOverdue();

            invoice.markPaid();

            assertThat(invoice.status()).isEqualTo(InvoiceStatus.PAID);
        }

        @Test
        void cannotMarkPaidFromDraft() {
            var invoice = draftWithSingleLine();

            assertThatThrownBy(invoice::markPaid).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void cannotCancelPaidInvoice() {
            var invoice = paidInvoice();

            assertThatThrownBy(invoice::cancel).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void cancelAllowedFromDraft() {
            var invoice = draftWithSingleLine();

            invoice.cancel();

            assertThat(invoice.status()).isEqualTo(InvoiceStatus.CANCELLED);
        }
    }

    @Nested
    class Refunds {

        @Test
        void partialRefundSetsPartiallyRefunded() {
            var invoice = paidInvoice();

            invoice.refund(new BigDecimal("50.00"));

            assertThat(invoice.status()).isEqualTo(InvoiceStatus.PARTIALLY_REFUNDED);
            assertThat(invoice.refundedAmount()).isEqualByComparingTo("50.00");
            assertThat(invoice.refundableBalance()).isEqualByComparingTo("60.00");
        }

        @Test
        void fullRefundSetsRefunded() {
            var invoice = paidInvoice();

            invoice.refund(new BigDecimal("110.00"));

            assertThat(invoice.status()).isEqualTo(InvoiceStatus.REFUNDED);
            assertThat(invoice.refundableBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void successiveRefundsAccumulate() {
            var invoice = paidInvoice();

            invoice.refund(new BigDecimal("40.00"));
            invoice.refund(new BigDecimal("70.00"));

            assertThat(invoice.status()).isEqualTo(InvoiceStatus.REFUNDED);
            assertThat(invoice.refundedAmount()).isEqualByComparingTo("110.00");
        }

        @Test
        void refundExceedingBalanceIsRejected() {
            var invoice = paidInvoice();

            assertThatThrownBy(() -> invoice.refund(new BigDecimal("110.01")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("exceeds refundable balance");
        }

        @Test
        void refundMustBePositive() {
            var invoice = paidInvoice();

            assertThatThrownBy(() -> invoice.refund(BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must be positive");
        }

        @Test
        void cannotRefundUnpaidInvoice() {
            var invoice = draftWithSingleLine();

            assertThatThrownBy(() -> invoice.refund(new BigDecimal("10.00")))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
