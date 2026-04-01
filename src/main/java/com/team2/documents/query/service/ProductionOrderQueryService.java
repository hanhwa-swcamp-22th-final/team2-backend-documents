package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.query.mapper.ProductionOrderQueryMapper;

@Service
public class ProductionOrderQueryService {

    private final ProductionOrderQueryMapper productionOrderQueryMapper;

    public ProductionOrderQueryService(ProductionOrderQueryMapper productionOrderQueryMapper) {
        this.productionOrderQueryMapper = productionOrderQueryMapper;
    }

    public ProductionOrder findById(String productionOrderId) {
        ProductionOrder productionOrder = productionOrderQueryMapper.findById(productionOrderId);
        if (productionOrder == null) {
            throw new IllegalArgumentException("생산지시서 정보를 찾을 수 없습니다.");
        }
        return productionOrder;
    }

    public List<ProductionOrder> findAll() {
        return productionOrderQueryMapper.findAll();
    }
}
