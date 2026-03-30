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

    public Shipment updateStatus(Long id, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("출하현황 정보를 찾을 수 없습니다."));
        shipment.setShipmentStatus(status);
        return shipment;
    }
}
