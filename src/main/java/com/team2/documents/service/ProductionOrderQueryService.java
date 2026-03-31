package com.team2.documents.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ProductionOrder;
import com.team2.documents.repository.ProductionOrderRepository;

@Service
public class ProductionOrderQueryService {

    private final ProductionOrderRepository productionOrderRepository;

    public ProductionOrderQueryService(ProductionOrderRepository productionOrderRepository) {
        this.productionOrderRepository = productionOrderRepository;
    }

    public List<ProductionOrder> findAll() {
        return productionOrderRepository.findAll();
    }

    public ProductionOrder findById(String productionOrderId) {
        return productionOrderRepository.findById(productionOrderId)
                .orElseThrow(() -> new IllegalArgumentException("생산지시서 정보를 찾을 수 없습니다."));
    }
}
