package com.team2.documents.command.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.command.domain.entity.PurchaseOrderItem;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    List<PurchaseOrderItem> findByPoId(Long poId);
}
