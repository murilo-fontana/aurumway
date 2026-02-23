package dev.murilofontana.aurumway.billing.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Entity
@Table(name = "journal_entries")
public class JournalEntryEntity {

    @Id
    @Column(name = "entry_id", nullable = false, updatable = false)
    private String entryId;

    @Column(name = "entry_number", nullable = false, unique = true, updatable = false)
    private String entryNumber;

    @Column(name = "entry_type", nullable = false, length = 30, updatable = false)
    private String entryType;

    @Column(name = "reference_id", nullable = false, updatable = false)
    private String referenceId;

    @Column(name = "currency", nullable = false, length = 3, updatable = false)
    private String currency;

    @Column(name = "description", nullable = false, length = 500, updatable = false)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<JournalLineEntity> lines = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (this.tenantId == null) {
            this.tenantId = dev.murilofontana.aurumway.billing.config.TenantContext.getCurrentTenant();
        }
    }

    protected JournalEntryEntity() {}

    public JournalEntryEntity(String entryId, String entryNumber, String entryType, String referenceId,
                              String currency, String description, Instant createdAt, List<JournalLineEntity> lines) {
        this.entryId = entryId;
        this.entryNumber = entryNumber;
        this.entryType = entryType;
        this.referenceId = referenceId;
        this.currency = currency;
        this.description = description;
        this.createdAt = createdAt;
        this.lines = lines;
        lines.forEach(line -> line.setEntry(this));
    }

    public String getTenantId() { return tenantId; }
    public String getEntryId() { return entryId; }
    public String getEntryNumber() { return entryNumber; }
    public String getEntryType() { return entryType; }
    public String getReferenceId() { return referenceId; }
    public String getCurrency() { return currency; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
    public List<JournalLineEntity> getLines() { return lines; }
}
