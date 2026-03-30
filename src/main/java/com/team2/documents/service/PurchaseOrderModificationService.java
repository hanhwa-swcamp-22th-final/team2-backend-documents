package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.Shipment;
import com.team2.documents.entity.enums.ShipmentStatus;
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

        if (ShipmentStatus.COMPLETED.equals(shipment.getShipmentStatus())) {
            throw new IllegalStateException("출하완료 상태의 PO는 수정할 수 없습니다.");
        }
    }

    public void validateDeletable(String poId) {
        Shipment shipment = shipmentRepository.findByPoId(poId)
                .orElseThrow(() -> new IllegalArgumentException("출하 정보를 찾을 수 없습니다."));

        if (ShipmentStatus.COMPLETED.equals(shipment.getShipmentStatus())) {
            throw new IllegalStateException("출하완료 상태의 PO는 삭제할 수 없습니다.");
        }
    }
}
