package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import com.team2.documents.command.domain.repository.ShipmentRepository;

@ExtendWith(MockitoExtension.class)
class ShipmentCommandServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private ShipmentCommandService shipmentCommandService;

    @Test
    @DisplayName("출하현황 목록 조회 시 전체 목록을 반환한다")
    void findAll_whenShipmentsExist_thenReturnsAllShipments() {
        // given
        Shipment shipment = new Shipment(1L, "PO2025-0001", ShipmentStatus.READY);
        when(shipmentRepository.findAll()).thenReturn(List.of(shipment));

        // when
        List<Shipment> shipments = shipmentCommandService.findAll();

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
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        // when
        Shipment result = shipmentCommandService.findById(1L);

        // then
        assertEquals(1L, result.getShipmentId());
        assertEquals("PO2025-0001", result.getPoId());
        assertEquals(ShipmentStatus.READY, result.getShipmentStatus());
    }

    @Test
    @DisplayName("출하현황 상태 변경 시 상태가 변경된다")
    void updateStatus_whenShipmentExists_thenChangesStatus() {
        // given
        Shipment shipment = new Shipment(1L, "PO2025-0001", ShipmentStatus.READY);
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Shipment result = shipmentCommandService.updateStatus(1L, ShipmentStatus.COMPLETED);

        // then
        assertEquals(ShipmentStatus.COMPLETED, result.getShipmentStatus());
    }

    @Test
    @DisplayName("출하현황 정보가 없으면 상태 변경 시 예외가 발생한다")
    void updateStatus_whenShipmentDoesNotExist_thenThrowsException() {
        // given
        when(shipmentRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> shipmentCommandService.updateStatus(1L, ShipmentStatus.COMPLETED));
    }

    @Test
    @DisplayName("PO ID로 출하현황 조회 시 해당 출하현황을 반환한다")
    void findByPoId_whenShipmentExists_thenReturnsShipment() {
        // given
        Shipment shipment = new Shipment(1L, "PO2025-0001", ShipmentStatus.READY);
        when(shipmentRepository.findByPoId("PO2025-0001")).thenReturn(Optional.of(shipment));

        // when
        Shipment result = shipmentCommandService.findByPoId("PO2025-0001");

        // then
        assertEquals("PO2025-0001", result.getPoId());
    }

    @Test
    @DisplayName("존재하지 않는 PO ID로 출하현황 조회 시 예외가 발생한다")
    void findByPoId_whenShipmentDoesNotExist_thenThrowsException() {
        // given
        when(shipmentRepository.findByPoId("NOT-EXIST")).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> shipmentCommandService.findByPoId("NOT-EXIST"));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 출하현황 조회 시 예외가 발생한다")
    void findById_whenShipmentDoesNotExist_thenThrowsException() {
        // given
        when(shipmentRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> shipmentCommandService.findById(999L));
    }
}
