package com.team2.documents.query.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.ProductionOrderQueryMapper;
import com.team2.documents.query.model.ProductionOrderView;

@Service
public class ProductionOrderQueryService {

    private final ProductionOrderQueryMapper productionOrderQueryMapper;

    public ProductionOrderQueryService(ProductionOrderQueryMapper productionOrderQueryMapper) {
        this.productionOrderQueryMapper = productionOrderQueryMapper;
    }

    public ProductionOrderView findById(String productionOrderId) {
        ProductionOrderView productionOrder = productionOrderQueryMapper.findById(productionOrderId);
        if (productionOrder == null) {
            throw new ResourceNotFoundException("생산지시서 정보를 찾을 수 없습니다.");
        }
        return productionOrder;
    }

    public List<ProductionOrderView> findAll() {
        return productionOrderQueryMapper.findAll();
    }
}
