package dev.murilofontana.aurumway.billing.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "journal_lines")
public class JournalLineEntity {

    @Id
    @Column(name = "line_id", nullable = false, updatable = false)
    private String lineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id", nullable = false)
    private JournalEntryEntity entry;

    @Column(name = "account_code", nullable = false, length = 50, updatable = false)
    private String accountCode;

    @Column(name = "debit", nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal debit;

    @Column(name = "credit", nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal credit;

    protected JournalLineEntity() {}

    public JournalLineEntity(String lineId, String accountCode, BigDecimal debit, BigDecimal credit) {
        this.lineId = lineId;
        this.accountCode = accountCode;
        this.debit = debit;
        this.credit = credit;
    }

    public void setEntry(JournalEntryEntity entry) { this.entry = entry; }

    public String getLineId() { return lineId; }
    public JournalEntryEntity getEntry() { return entry; }
    public String getAccountCode() { return accountCode; }
    public BigDecimal getDebit() { return debit; }
    public BigDecimal getCredit() { return credit; }
}
