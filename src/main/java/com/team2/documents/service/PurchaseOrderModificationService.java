package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.Shipment;
import com.team2.documents.repository.ShipmentRepository;

@Service
public class PurchaseOrderModificationService {

    private final ShipmentRepository shipmentRepository;

    public PurchaseOrderModificationService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public void validateModifiable(String poId) {
        Shipment shipment = shipmentRepository.findByPoId(poId)
                .orElseThrow(() -> new IllegalArgumentException("출하 정보를 찾을 수 없습니다."));

        PurchaseOrder purchaseOrder = new PurchaseOrder(poId);
        purchaseOrder.validateModifiable(shipment.getShipmentStatus());
    }

    public void validateDeletable(String poId) {
        Shipment shipment = shipmentRepository.findByPoId(poId)
                .orElseThrow(() -> new IllegalArgumentException("출하 정보를 찾을 수 없습니다."));

        PurchaseOrder purchaseOrder = new PurchaseOrder(poId);
        purchaseOrder.validateDeletable(shipment.getShipmentStatus());
    }
}
