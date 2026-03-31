package com.team2.documents.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.Shipment;
import com.team2.documents.entity.enums.ShipmentStatus;
import com.team2.documents.query.mapper.ShipmentQueryMapper;

@ExtendWith(MockitoExtension.class)
class ShipmentQueryServiceTest {

    @Mock
    private ShipmentQueryMapper shipmentQueryMapper;

    @InjectMocks
    private ShipmentQueryService shipmentQueryService;

    @Test
    @DisplayName("PO ID로 출하현황 조회 시 해당 출하현황을 반환한다")
    void findByPoId_whenShipmentExists_thenReturnsShipment() {
        // given
        Shipment shipment = new Shipment(1L, "PO2025-0001", ShipmentStatus.READY);
        when(shipmentQueryMapper.findByPoId("PO2025-0001")).thenReturn(shipment);

        // when
        Shipment result = shipmentQueryService.findByPoId("PO2025-0001");

        // then
        assertEquals(1L, result.getShipmentId());
        assertEquals("PO2025-0001", result.getPoId());
        assertEquals(ShipmentStatus.READY, result.getShipmentStatus());
    }

    @Test
    @DisplayName("존재하지 않는 PO ID로 출하현황 조회 시 예외를 던진다")
    void findByPoId_whenShipmentNotExists_thenThrowsException() {
        // given
        when(shipmentQueryMapper.findByPoId("NOT-EXIST")).thenReturn(null);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> shipmentQueryService.findByPoId("NOT-EXIST"));
    }
}
