package com.team2.documents.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
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

    @Test
    @DisplayName("출하현황 ID로 조회 시 해당 출하현황을 반환한다")
    void findById_whenShipmentExists_thenReturnsShipment() {
        // given
        Shipment shipment = new Shipment(1L, "PO2025-0001", ShipmentStatus.READY);
        when(shipmentQueryMapper.findById(1L)).thenReturn(shipment);

        // when
        Shipment result = shipmentQueryService.findById(1L);

        // then
        assertEquals(1L, result.getShipmentId());
    }

    @Test
    @DisplayName("존재하지 않는 출하현황 ID로 조회 시 예외를 던진다")
    void findById_whenShipmentNotExists_thenThrowsException() {
        // given
        when(shipmentQueryMapper.findById(999L)).thenReturn(null);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> shipmentQueryService.findById(999L));
    }

    @Test
    @DisplayName("전체 출하현황 목록을 조회한다")
    void findAll_whenShipmentsExist_thenReturnsAll() {
        // given
        Shipment shipment = new Shipment(1L, "PO2025-0001", ShipmentStatus.READY);
        when(shipmentQueryMapper.findAll()).thenReturn(List.of(shipment));

        // when
        List<Shipment> result = shipmentQueryService.findAll();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
