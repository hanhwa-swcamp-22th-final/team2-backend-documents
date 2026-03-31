package com.team2.documents.command.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.entity.ProductionOrder;
import com.team2.documents.command.repository.ProductionOrderRepository;

@Service
@Transactional
public class ProductionOrderCommandService {

    private final ProductionOrderRepository productionOrderRepository;

    public ProductionOrderCommandService(ProductionOrderRepository productionOrderRepository) {
        this.productionOrderRepository = productionOrderRepository;
    }

    public List<ProductionOrder> findAll() {
        return productionOrderRepository.findAll();
    }

    public ProductionOrder findById(String id) {
        return productionOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("생산지시서 정보를 찾을 수 없습니다."));
    }

    public ProductionOrder save(ProductionOrder productionOrder) {
        return productionOrderRepository.save(productionOrder);
    }
}
