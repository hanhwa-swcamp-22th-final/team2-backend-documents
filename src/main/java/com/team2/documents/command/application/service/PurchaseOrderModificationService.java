package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;

@Service
@Transactional
public class PurchaseOrderModificationService {

    private final ShipmentCommandService shipmentCommandService;

    public PurchaseOrderModificationService(ShipmentCommandService shipmentCommandService) {
        this.shipmentCommandService = shipmentCommandService;
    }

    public void validateModifiable(String poId) {
        Shipment shipment = shipmentCommandService.findByPoId(poId);

        if (ShipmentStatus.COMPLETED.equals(shipment.getShipmentStatus())) {
            throw new IllegalStateException("출하완료 상태의 PO는 수정할 수 없습니다.");
        }
    }

    public void validateDeletable(String poId) {
        Shipment shipment = shipmentCommandService.findByPoId(poId);

        if (ShipmentStatus.COMPLETED.equals(shipment.getShipmentStatus())) {
            throw new IllegalStateException("출하완료 상태의 PO는 삭제할 수 없습니다.");
        }
    }
}
