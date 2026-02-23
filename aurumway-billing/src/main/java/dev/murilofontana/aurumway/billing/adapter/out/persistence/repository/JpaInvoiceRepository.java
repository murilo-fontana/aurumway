package dev.murilofontana.aurumway.billing.adapter.out.persistence.repository;

import dev.murilofontana.aurumway.billing.adapter.out.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JpaInvoiceRepository extends JpaRepository<InvoiceEntity, String> {

    Optional<InvoiceEntity> findByInvoiceId(String invoiceId);

    List<InvoiceEntity> findByStatus(String status);

    List<InvoiceEntity> findByCustomerId(String customerId);

    @Query(value = "SELECT * FROM invoices i WHERE " +
           "i.tenant_id = :tenantId AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:customerId IS NULL OR i.customer_id = :customerId) AND " +
           "(CAST(:fromDate AS TIMESTAMPTZ) IS NULL OR i.created_at >= CAST(:fromDate AS TIMESTAMPTZ)) AND " +
           "(CAST(:toDate AS TIMESTAMPTZ) IS NULL OR i.created_at <= CAST(:toDate AS TIMESTAMPTZ))",
           nativeQuery = true)
    List<InvoiceEntity> findWithFilters(String tenantId, String status, String customerId, Instant fromDate, Instant toDate);

    @Query(value = "SELECT nextval('invoice_number_seq')", nativeQuery = true)
    long nextInvoiceNumber();
}
