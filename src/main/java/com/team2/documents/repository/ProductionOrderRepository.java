package com.team2.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.entity.ProductionOrder;

public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, String> {
}
