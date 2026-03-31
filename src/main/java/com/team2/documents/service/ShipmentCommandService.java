package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.Shipment;
import com.team2.documents.entity.enums.ShipmentStatus;
import com.team2.documents.repository.ShipmentRepository;

@Service
public class ShipmentCommandService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentCommandService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Shipment updateStatus(Long shipmentId, ShipmentStatus targetShipmentStatus) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("출하현황 정보를 찾을 수 없습니다."));
        shipment.setShipmentStatus(targetShipmentStatus);
        return shipmentRepository.save(shipment);
    }
}
