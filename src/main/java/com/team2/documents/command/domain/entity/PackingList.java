package com.team2.documents.command.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "packing_lists")
public class PackingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pl_id", nullable = false)
    private Long packingListId;

    @Column(name = "pl_code", nullable = false, unique = true, length = 30)
    private String plCode;

    @Column(name = "po_id", nullable = false)
    private Long poId;

    @Column(name = "pl_invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "pl_gross_weight")
    private java.math.BigDecimal grossWeight;

    @Column(name = "pl_status")
    private String status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public PackingList() {
    }

    public Long getPackingListId() {
        return packingListId;
    }

    public String getPlCode() {
        return plCode;
    }

    public String getPlId() {
        return plCode;
    }

    public void setPlId(String plId) {
        this.plCode = plId;
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

    public java.math.BigDecimal getGrossWeight() {
        return grossWeight;
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
