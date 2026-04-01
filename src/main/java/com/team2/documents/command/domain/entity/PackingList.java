package com.team2.documents.command.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "packing_lists")
public class PackingList {

    @Id
    @Column(name = "pl_id", nullable = false, length = 30)
    private String plId;

    @Column(name = "po_id", nullable = false, length = 30)
    private String poId;

    @Column(name = "pl_invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "pl_status")
    private String status;

    @Column(name = "pl_linked_documents", columnDefinition = "TEXT")
    private String linkedDocuments;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public PackingList() {
    }

    public String getPlId() {
        return plId;
    }

    public void setPlId(String plId) {
        this.plId = plId;
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
