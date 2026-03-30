package com.team2.documents.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team2.documents.entity.Shipment;
import com.team2.documents.entity.enums.ShipmentStatus;

@DataJpaTest
class ShipmentRepositoryTest {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Test
    @DisplayName("PO 번호로 출하 상태를 조회할 수 있다")
    void findByPoId_whenShipmentExists_thenReturnsShipment() {
        // given
        shipmentRepository.save(new Shipment(1L, "PO2025-0001", ShipmentStatus.COMPLETED));

        // when
        Optional<Shipment> result = shipmentRepository.findByPoId("PO2025-0001");

        // then
        assertTrue(result.isPresent());
        assertEquals(ShipmentStatus.COMPLETED, result.get().getShipmentStatus());
    }
}
