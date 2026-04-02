package com.team2.documents.command.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "commercial_invoices")
public class CommercialInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ci_id", nullable = false)
    private Long commercialInvoiceId;

    @Column(name = "ci_code", nullable = false, unique = true, length = 30)
    private String ciCode;

    @Column(name = "po_id", nullable = false)
    private Long poId;

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

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public CommercialInvoice() {
    }

    public Long getCommercialInvoiceId() {
        return commercialInvoiceId;
    }

    public String getCiCode() {
        return ciCode;
    }

    public String getCiId() {
        return ciCode;
    }

    public void setCiId(String ciId) {
        this.ciCode = ciId;
    }

    public Long getPoId() {
        return poId;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public Integer getClientId() {
        return clientId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
