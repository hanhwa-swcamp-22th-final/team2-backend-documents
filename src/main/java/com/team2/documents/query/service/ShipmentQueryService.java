package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.dto.PagedResult;
import com.team2.documents.query.mapper.ShipmentQueryMapper;
import com.team2.documents.query.model.ShipmentView;

@Service
public class ShipmentQueryService {

    private final ShipmentQueryMapper shipmentQueryMapper;

    public ShipmentQueryService(ShipmentQueryMapper shipmentQueryMapper) {
        this.shipmentQueryMapper = shipmentQueryMapper;
    }

    public ShipmentView findById(Long shipmentId) {
        ShipmentView shipment = shipmentQueryMapper.findById(shipmentId);
        if (shipment == null) {
            throw new ResourceNotFoundException("출하현황 정보를 찾을 수 없습니다.");
        }
        return shipment;
    }

    public List<ShipmentView> findAll() {
        return shipmentQueryMapper.findAll();
    }

    public PagedResult<ShipmentView> findAll(int page, int size) {
        int offset = page * size;
        List<ShipmentView> content = shipmentQueryMapper.findPage(offset, size);
        long total = shipmentQueryMapper.countAll();
        return new PagedResult<>(content, total);
    }

    public ShipmentView findByPoId(String poId) {
        ShipmentView shipment = shipmentQueryMapper.findByPoId(poId);
        if (shipment == null) {
            throw new ResourceNotFoundException("해당 PO의 출하현황 정보를 찾을 수 없습니다.");
        }
        return shipment;
    }
}
