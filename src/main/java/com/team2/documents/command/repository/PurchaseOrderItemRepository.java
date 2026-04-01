package com.team2.documents.command.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.entity.PurchaseOrderItem;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    List<PurchaseOrderItem> findByPoId(String poId);
}
