package com.team2.documents.command.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import lombok.Setter;

@Setter
@Entity
@Table(name = "proforma_invoices")
public class ProformaInvoice {

    @Id
    @Column(name = "pi_id", nullable = false, length = 30)
    private String piId;

    @Enumerated(EnumType.STRING)
    @Column(name = "pi_status", nullable = false)
    private ProformaInvoiceStatus status;

    protected ProformaInvoice() {
    }

    public ProformaInvoice(String piId, ProformaInvoiceStatus status) {
        this.piId = piId;
        this.status = status;
    }

    public String getPiId() {
        return piId;
    }

    public ProformaInvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(ProformaInvoiceStatus status) {
        this.status = status;
    }
}
