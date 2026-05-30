package dev.murilofontana.aurumway.contracts.domain.model;

import dev.murilofontana.aurumway.contracts.domain.valueobject.BillingCycle;
import dev.murilofontana.aurumway.contracts.domain.valueobject.ContractStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContractTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 12, 31);

    private static Contract draft(BillingCycle cycle) {
        var item = ContractItem.create("Subscription", 1, new BigDecimal("100.00"), new BigDecimal("0.10"));
        return Contract.createDraft("cust-1", "Acme", "USD", cycle, START, END, List.of(item));
    }

    private static Contract active(BillingCycle cycle) {
        var contract = draft(cycle);
        contract.activate();
        return contract;
    }

    @Nested
    class Creation {

        @Test
        void createsDraftWithNoNextBillingDate() {
            var contract = draft(BillingCycle.MONTHLY);

            assertThat(contract.status()).isEqualTo(ContractStatus.DRAFT);
            assertThat(contract.nextBillingDate()).isNull();
        }

        @Test
        void rejectsEmptyItems() {
            assertThatThrownBy(() -> Contract.createDraft("c", "n", "USD", BillingCycle.MONTHLY, START, END, List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("at least one item");
        }

        @Test
        void rejectsEndBeforeStart() {
            var item = ContractItem.create("X", 1, new BigDecimal("10.00"), BigDecimal.ZERO);
            assertThatThrownBy(() -> Contract.createDraft("c", "n", "USD", BillingCycle.MONTHLY, END, START, List.of(item)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("End date must be after start date");
        }

        @Test
        void totalPerCycleSumsItems() {
            var contract = draft(BillingCycle.MONTHLY);

            assertThat(contract.totalPerCycle()).isEqualByComparingTo("110.00");
        }
    }

    @Nested
    class Lifecycle {

        @Test
        void activateSetsNextBillingDateToStart() {
            var contract = draft(BillingCycle.MONTHLY);

            contract.activate();

            assertThat(contract.status()).isEqualTo(ContractStatus.ACTIVE);
            assertThat(contract.nextBillingDate()).isEqualTo(START);
        }

        @Test
        void cannotActivateTwice() {
            var contract = active(BillingCycle.MONTHLY);

            assertThatThrownBy(contract::activate).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void suspendAndResume() {
            var contract = active(BillingCycle.MONTHLY);

            contract.suspend();
            assertThat(contract.status()).isEqualTo(ContractStatus.SUSPENDED);

            contract.resume();
            assertThat(contract.status()).isEqualTo(ContractStatus.ACTIVE);
        }

        @Test
        void cannotSuspendDraft() {
            var contract = draft(BillingCycle.MONTHLY);

            assertThatThrownBy(contract::suspend).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void terminateClearsNextBillingDate() {
            var contract = active(BillingCycle.MONTHLY);

            contract.terminate();

            assertThat(contract.status()).isEqualTo(ContractStatus.TERMINATED);
            assertThat(contract.nextBillingDate()).isNull();
        }

        @Test
        void markExpiredOnlyFromActive() {
            var contract = active(BillingCycle.MONTHLY);

            contract.markExpired();

            assertThat(contract.status()).isEqualTo(ContractStatus.EXPIRED);
        }
    }

    @Nested
    class Billing {

        @Test
        void activeContractIsDueWhenBillingDateReached() {
            var contract = active(BillingCycle.MONTHLY);

            assertThat(contract.isDueFoBilling(START)).isTrue();
            assertThat(contract.isDueFoBilling(START.minusDays(1))).isFalse();
        }

        @Test
        void suspendedContractIsNotDue() {
            var contract = active(BillingCycle.MONTHLY);
            contract.suspend();

            assertThat(contract.isDueFoBilling(START)).isFalse();
        }

        @Test
        void advanceMonthly() {
            var contract = active(BillingCycle.MONTHLY);

            contract.advanceBillingDate();

            assertThat(contract.nextBillingDate()).isEqualTo(START.plusMonths(1));
        }

        @Test
        void advanceQuarterly() {
            var contract = active(BillingCycle.QUARTERLY);

            contract.advanceBillingDate();

            assertThat(contract.nextBillingDate()).isEqualTo(START.plusMonths(3));
        }

        @Test
        void advanceAnnualBeyondEndClearsBillingDate() {
            var contract = active(BillingCycle.ANNUAL);

            contract.advanceBillingDate();

            assertThat(contract.nextBillingDate()).isNull();
        }

        @Test
        void notDueAfterEndDate() {
            var contract = active(BillingCycle.MONTHLY);

            assertThat(contract.isDueFoBilling(END.plusDays(1))).isFalse();
        }
    }
}
