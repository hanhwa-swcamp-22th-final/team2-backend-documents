package com.team2.documents.command.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.entity.Shipment;
import com.team2.documents.entity.enums.ShipmentStatus;
import com.team2.documents.command.repository.ShipmentRepository;

@Service
@Transactional
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

    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    public Shipment findById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("출하현황 정보를 찾을 수 없습니다."));
    }

    public Shipment findByPoId(String poId) {
        return shipmentRepository.findByPoId(poId)
                .orElseThrow(() -> new IllegalArgumentException("해당 PO의 출하현황 정보를 찾을 수 없습니다."));
    }
}
