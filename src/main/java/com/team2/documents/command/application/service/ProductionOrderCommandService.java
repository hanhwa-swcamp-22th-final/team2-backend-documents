package com.team2.documents.command.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;

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
        return productionOrderRepository.findByProductionOrderCode(id)
                .orElseThrow(() -> new IllegalArgumentException("생산지시서 정보를 찾을 수 없습니다."));
    }

    public ProductionOrder save(ProductionOrder productionOrder) {
        return productionOrderRepository.save(productionOrder);
    }

    /** 생산완료 처리. DB enum 값 'completed' 로 상태 변경. */
    public ProductionOrder complete(String productionOrderCode) {
        ProductionOrder po = productionOrderRepository.findByProductionOrderCode(productionOrderCode)
                .orElseThrow(() -> new IllegalArgumentException("생산지시서 정보를 찾을 수 없습니다."));
        po.setStatus("completed");
        return productionOrderRepository.save(po);
    }
}
