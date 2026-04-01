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

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "pl_gross_weight")
    private java.math.BigDecimal grossWeight;

    @Column(name = "pl_status")
    private String status;

    @Column(name = "pl_client_name")
    private String clientName;

    @Column(name = "pl_client_address", columnDefinition = "TEXT")
    private String clientAddress;

    @Column(name = "pl_country")
    private String country;

    @Column(name = "pl_payment_terms")
    private String paymentTerms;

    @Column(name = "pl_port_of_discharge")
    private String portOfDischarge;

    @Column(name = "pl_buyer")
    private String buyer;

    @Column(name = "pl_items_snapshot", columnDefinition = "TEXT")
    private String itemsSnapshot;

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

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public Integer getClientId() {
        return clientId;
    }

    public java.math.BigDecimal getGrossWeight() {
        return grossWeight;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public String getStatus() {
        return status;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String getCountry() {
        return country;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getItemsSnapshot() {
        return itemsSnapshot;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLinkedDocuments() {
        return linkedDocuments;
    }

    public void setLinkedDocuments(String linkedDocuments) {
        this.linkedDocuments = linkedDocuments;
    }
}
