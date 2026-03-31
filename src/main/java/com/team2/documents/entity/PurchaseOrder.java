package com.team2.documents.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.team2.documents.entity.enums.PurchaseOrderStatus;
import lombok.Setter;

@Setter
@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @Column(name = "po_id", nullable = false, length = 30)
    private String poId;

    @Enumerated(EnumType.STRING)
    @Column(name = "po_status", nullable = false)
    private PurchaseOrderStatus status;

    protected PurchaseOrder() {
    }

    public PurchaseOrder(String poId) {
        this.poId = poId;
        this.status = PurchaseOrderStatus.DRAFT;
    }

    public PurchaseOrder(String poId, PurchaseOrderStatus status) {
        this.poId = poId;
        this.status = status;
    }

    public String getPoId() {
        return poId;
    }

    public PurchaseOrderStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseOrderStatus status) {
        this.status = status;
    }
}
