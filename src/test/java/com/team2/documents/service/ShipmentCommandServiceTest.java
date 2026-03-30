package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.Shipment;
import com.team2.documents.entity.enums.ShipmentStatus;
import com.team2.documents.repository.ShipmentRepository;

@ExtendWith(MockitoExtension.class)
class ShipmentCommandServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private ShipmentCommandService shipmentCommandService;

    @Test
    @DisplayName("출하현황 상태 변경 시 상태가 변경된다")
    void updateStatus_whenShipmentExists_thenChangesStatus() {
        // given
        Shipment shipment = new Shipment(1L, "PO2025-0001", ShipmentStatus.READY);
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

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
}
