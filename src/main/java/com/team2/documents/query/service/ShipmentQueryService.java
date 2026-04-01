package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.query.mapper.ShipmentQueryMapper;

@Service
public class ShipmentQueryService {

    private final ShipmentQueryMapper shipmentQueryMapper;

    public ShipmentQueryService(ShipmentQueryMapper shipmentQueryMapper) {
        this.shipmentQueryMapper = shipmentQueryMapper;
    }

    public Shipment findById(Long shipmentId) {
        Shipment shipment = shipmentQueryMapper.findById(shipmentId);
        if (shipment == null) {
            throw new IllegalArgumentException("출하현황 정보를 찾을 수 없습니다.");
        }
        return shipment;
    }

    public List<Shipment> findAll() {
        return shipmentQueryMapper.findAll();
    }

    public Shipment findByPoId(String poId) {
        Shipment shipment = shipmentQueryMapper.findByPoId(poId);
        if (shipment == null) {
            throw new IllegalArgumentException("해당 PO의 출하현황 정보를 찾을 수 없습니다.");
        }
        return shipment;
    }
}
