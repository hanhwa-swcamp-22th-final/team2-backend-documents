package com.team2.documents.command.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.team2.documents.command.domain.entity.converter.ShipmentStatusConverter;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import lombok.Setter;

@Setter
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @Column(name = "shipment_id")
    private Long shipmentId;

    @Column(name = "po_id", nullable = false)
    private Long poId;

    @Column(name = "shipment_order_id")
    private Long shipmentOrderId;

    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "shipment_request_date")
    private LocalDate shipmentRequestDate;

    @Column(name = "shipment_due_date")
    private LocalDate shipmentDueDate;

    @Transient
    private String poCode;

    @Transient
    private String shipmentOrderCode;

    @Convert(converter = ShipmentStatusConverter.class)
    @Column(name = "shipment_status", nullable = false)
    private ShipmentStatus shipmentStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    protected Shipment() {
    }

    public Shipment(Long shipmentId, String poCode, ShipmentStatus shipmentStatus) {
        this(shipmentId, 0L, poCode, shipmentStatus);
    }

    public Shipment(Long shipmentId, Long poId, String poCode, ShipmentStatus shipmentStatus) {
        this.shipmentId = shipmentId;
        this.poId = poId;
        this.poCode = poCode;
        this.shipmentStatus = shipmentStatus;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public String getPoId() {
        return poCode != null ? poCode : (poId == null ? null : String.valueOf(poId));
    }

    public Long getPurchaseOrderId() {
        return poId;
    }

    public Long getShipmentOrderId() {
        return shipmentOrderId;
    }

    public String getPoCode() {
        return poCode;
    }

    public String getShipmentOrderCode() {
        return shipmentOrderCode;
    }

    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public Integer getClientId() {
        return clientId;
    }

    public LocalDate getShipmentRequestDate() {
        return shipmentRequestDate;
    }

    public LocalDate getShipmentDueDate() {
        return shipmentDueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
