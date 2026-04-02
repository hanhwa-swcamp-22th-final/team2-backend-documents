package com.team2.documents.command.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team2.documents.command.domain.entity.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByPoId(Long poId);

    @Query(value = """
            SELECT s.*
            FROM shipments s
            JOIN purchase_orders p ON s.po_id = p.po_id
            WHERE p.po_code = :poCode
            """, nativeQuery = true)
    Optional<Shipment> findByPoCode(@Param("poCode") String poCode);

    default Optional<Shipment> findByPoId(String poCode) {
        return findByPoCode(poCode);
    }
}
