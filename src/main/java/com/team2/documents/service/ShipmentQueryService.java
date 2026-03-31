package com.team2.documents.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.Shipment;
import com.team2.documents.mapper.ShipmentQueryMapper;

@Service
public class ShipmentQueryService {

    private final ShipmentQueryMapper shipmentQueryMapper;

    public ShipmentQueryService(ShipmentQueryMapper shipmentQueryMapper) {
        this.shipmentQueryMapper = shipmentQueryMapper;
    }

    public List<Shipment> findAll() {
        return shipmentQueryMapper.findAll();
    }

    public Shipment findById(Long id) {
        Shipment shipment = shipmentQueryMapper.findById(id);
        if (shipment == null) {
            throw new IllegalArgumentException("출하현황 정보를 찾을 수 없습니다.");
        }
        return shipment;
    }

    public Shipment findByPoId(String poId) {
        Shipment shipment = shipmentQueryMapper.findByPoId(poId);
        if (shipment == null) {
            throw new IllegalArgumentException("해당 PO의 출하현황 정보를 찾을 수 없습니다.");
        }
        return shipment;
    }
}
