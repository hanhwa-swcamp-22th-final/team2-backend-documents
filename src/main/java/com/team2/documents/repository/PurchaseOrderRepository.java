package com.team2.documents.repository;

import java.util.Optional;

import com.team2.documents.entity.PurchaseOrder;

public interface PurchaseOrderRepository {

    Optional<PurchaseOrder> findById(String poId);
}
