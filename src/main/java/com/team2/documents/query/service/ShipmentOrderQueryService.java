package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.query.mapper.ShipmentOrderQueryMapper;

@Service
public class ShipmentOrderQueryService {

    private final ShipmentOrderQueryMapper shipmentOrderQueryMapper;

    public ShipmentOrderQueryService(ShipmentOrderQueryMapper shipmentOrderQueryMapper) {
        this.shipmentOrderQueryMapper = shipmentOrderQueryMapper;
    }

    public ShipmentOrder findById(String shipmentOrderId) {
        ShipmentOrder shipmentOrder = shipmentOrderQueryMapper.findById(shipmentOrderId);
        if (shipmentOrder == null) {
            throw new IllegalArgumentException("출하지시서 정보를 찾을 수 없습니다.");
        }
        return shipmentOrder;
    }

    public List<ShipmentOrder> findAll() {
        return shipmentOrderQueryMapper.findAll();
    }
}
