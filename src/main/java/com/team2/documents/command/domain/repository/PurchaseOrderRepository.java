package com.team2.documents.command.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.team2.documents.command.domain.entity.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPoCode(String poCode);
}
