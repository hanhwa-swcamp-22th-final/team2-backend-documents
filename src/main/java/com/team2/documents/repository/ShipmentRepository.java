package com.team2.documents.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.entity.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByPoId(String poId);
}
