package com.team2.documents.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ProductionOrder;
import com.team2.documents.mapper.ProductionOrderQueryMapper;

@Service
public class ProductionOrderQueryService {

    private final ProductionOrderQueryMapper productionOrderQueryMapper;

    public ProductionOrderQueryService(ProductionOrderQueryMapper productionOrderQueryMapper) {
        this.productionOrderQueryMapper = productionOrderQueryMapper;
    }

    public List<ProductionOrder> findAll() {
        return productionOrderQueryMapper.findAll();
    }

    public ProductionOrder findById(String id) {
        ProductionOrder productionOrder = productionOrderQueryMapper.findById(id);
        if (productionOrder == null) {
            throw new IllegalArgumentException("생산지시서 정보를 찾을 수 없습니다.");
        }
        return productionOrder;
    }
}
