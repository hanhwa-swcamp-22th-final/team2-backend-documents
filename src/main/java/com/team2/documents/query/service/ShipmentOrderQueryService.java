package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.ShipmentOrderQueryMapper;
import com.team2.documents.query.model.ShipmentOrderView;

@Service
public class ShipmentOrderQueryService {

    private final ShipmentOrderQueryMapper shipmentOrderQueryMapper;

    public ShipmentOrderQueryService(ShipmentOrderQueryMapper shipmentOrderQueryMapper) {
        this.shipmentOrderQueryMapper = shipmentOrderQueryMapper;
    }

    public ShipmentOrderView findById(String shipmentOrderId) {
        ShipmentOrderView shipmentOrder = shipmentOrderQueryMapper.findById(shipmentOrderId);
        if (shipmentOrder == null) {
            throw new ResourceNotFoundException("출하지시서 정보를 찾을 수 없습니다.");
        }
        return shipmentOrder;
    }

    public List<ShipmentOrderView> findAll() {
        return shipmentOrderQueryMapper.findAll();
    }
}
