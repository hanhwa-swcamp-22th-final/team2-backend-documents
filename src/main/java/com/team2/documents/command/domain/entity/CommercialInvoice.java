package com.team2.documents.command.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "commercial_invoices")
public class CommercialInvoice {

    @Id
    @Column(name = "ci_id", nullable = false, length = 30)
    private String ciId;

    @Column(name = "po_id", nullable = false, length = 30)
    private String poId;

    @Column(name = "ci_invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "currency_id")
    private Integer currencyId;

    @Column(name = "ci_total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "ci_status")
    private String status;

    @Column(name = "ci_client_name")
    private String clientName;

    @Column(name = "ci_client_address", columnDefinition = "TEXT")
    private String clientAddress;

    @Column(name = "ci_country")
    private String country;

    @Column(name = "ci_currency_code")
    private String currencyCode;

    @Column(name = "ci_payment_terms")
    private String paymentTerms;

    @Column(name = "ci_port_of_discharge")
    private String portOfDischarge;

    @Column(name = "ci_buyer")
    private String buyer;

    @Column(name = "ci_items_snapshot", columnDefinition = "TEXT")
    private String itemsSnapshot;

    @Column(name = "ci_linked_documents", columnDefinition = "TEXT")
    private String linkedDocuments;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public CommercialInvoice() {
    }

    public String getCiId() {
        return ciId;
    }

    public void setCiId(String ciId) {
        this.ciId = ciId;
    }

    public String getPoId() {
        return poId;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
