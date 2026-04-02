package com.team2.documents.command.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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

    @Transient
    private String poCode;

    @Transient
    private String shipmentOrderCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false)
    private ShipmentStatus shipmentStatus;

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
}
