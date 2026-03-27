package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.Shipment;
import com.team2.documents.entity.ShipmentStatus;
import com.team2.documents.repository.ShipmentRepository;

@ExtendWith(MockitoExtension.class)
class ShipmentQueryServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private ShipmentQueryService shipmentQueryService;

    @Test
    @DisplayName("출하현황 목록 조회 시 전체 목록을 반환한다")
    void findAll_whenShipmentsExist_thenReturnsAllShipments() {
        // given
        Shipment shipment = new Shipment(1L, "PO2025-0001", ShipmentStatus.READY);
        when(shipmentRepository.findAll()).thenReturn(List.of(shipment));

        // when
        List<Shipment> shipments = shipmentQueryService.findAll();

        // then
        assertEquals(1, shipments.size());
        assertEquals("PO2025-0001", shipments.get(0).getPoId());
        assertEquals(ShipmentStatus.READY, shipments.get(0).getShipmentStatus());
    }

    @Test
    @DisplayName("출하현황 단건 조회 시 해당 출하현황을 반환한다")
    void findById_whenShipmentExists_thenReturnsShipment() {
        // given
        Shipment shipment = new Shipment(1L, "PO2025-0001", ShipmentStatus.READY);
        when(shipmentRepository.findById(1L)).thenReturn(java.util.Optional.of(shipment));

        // when
        Shipment result = shipmentQueryService.findById(1L);

        // then
        assertEquals(1L, result.getShipmentId());
        assertEquals("PO2025-0001", result.getPoId());
        assertEquals(ShipmentStatus.READY, result.getShipmentStatus());
    }
}
