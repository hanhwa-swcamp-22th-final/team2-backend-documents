package com.team2.documents.command.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipment_orders")
public class ShipmentOrder {

    @Id
    @Column(name = "shipment_order_id", nullable = false, length = 30)
    private String shipmentOrderId;

    @Column(name = "po_id", nullable = false, length = 30)
    private String poId;

    @Column(name = "shipment_issue_date")
    private LocalDate issueDate;

    @Column(name = "shipment_status")
    private String status;

    @Column(name = "shipment_linked_documents", columnDefinition = "TEXT")
    private String linkedDocuments;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ShipmentOrder() {
    }

    public String getShipmentOrderId() {
        return shipmentOrderId;
    }

    public void setShipmentOrderId(String shipmentOrderId) {
        this.shipmentOrderId = shipmentOrderId;
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
