package com.team2.documents.command.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.command.domain.entity.ShipmentOrder;

public interface ShipmentOrderJpaRepository extends JpaRepository<ShipmentOrder, Long> {

    Optional<ShipmentOrder> findByShipmentOrderCode(String shipmentOrderCode);

    Optional<ShipmentOrder> findByPoId(Long poId);
}
