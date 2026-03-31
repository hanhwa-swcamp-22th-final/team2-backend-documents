package com.team2.documents.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.team2.documents.entity.enums.ShipmentStatus;
import lombok.Setter;

@Setter
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @Column(name = "shipment_id")
    private Long shipmentId;

    @Column(name = "po_id", nullable = false)
    private String poId;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false)
    private ShipmentStatus shipmentStatus;

    protected Shipment() {
    }

    public Shipment(Long shipmentId, String poId, ShipmentStatus shipmentStatus) {
        this.shipmentId = shipmentId;
        this.poId = poId;
        this.shipmentStatus = shipmentStatus;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public String getPoId() {
        return poId;
    }

    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }
}
