package com.team2.documents.repository;

import java.util.List;
import java.util.Optional;

public interface ProductionOrderRepository {

    void createFromPurchaseOrder(String poId);

    List<com.team2.documents.entity.ProductionOrder> findAll();

    Optional<com.team2.documents.entity.ProductionOrder> findById(Long id);
}
