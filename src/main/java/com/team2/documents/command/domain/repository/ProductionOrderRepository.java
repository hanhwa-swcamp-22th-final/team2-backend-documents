package com.team2.documents.command.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.command.domain.entity.ProductionOrder;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    Optional<ProductionOrder> findByProductionOrderCode(String productionOrderCode);

    default Optional<ProductionOrder> findById(String productionOrderCode) {
        return findByProductionOrderCode(productionOrderCode);
    }

    Optional<ProductionOrder> findByPoId(Long poId);
}
