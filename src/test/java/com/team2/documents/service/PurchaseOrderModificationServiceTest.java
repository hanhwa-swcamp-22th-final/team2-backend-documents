package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import com.team2.documents.entity.ShipmentStatus;
import com.team2.documents.repository.ShipmentRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderModificationServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private PurchaseOrderModificationService purchaseOrderModificationService;

    @Test
    @DisplayName("출하준비 상태인 PO는 수정 요청 시 예외가 발생하지 않는다")
    void validateModifiable_whenShipmentReady_thenPasses() {
        // given
        String poId = "PO2025-0001";
        Shipment shipment = new Shipment(1L, poId, ShipmentStatus.READY);
        when(shipmentRepository.findByPoId(poId)).thenReturn(Optional.of(shipment));

        // when & then
        assertDoesNotThrow(() -> purchaseOrderModificationService.validateModifiable(poId));
    }

    @Test
    @DisplayName("출하완료 상태인 PO는 수정 요청 시 예외가 발생한다")
    void validateModifiable_whenShipmentCompleted_thenThrowsException() {
        // given
        String poId = "PO2025-0001";
        Shipment shipment = new Shipment(1L, poId, ShipmentStatus.COMPLETED);
        when(shipmentRepository.findByPoId(poId)).thenReturn(Optional.of(shipment));

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrderModificationService.validateModifiable(poId));
    }

    @Test
    @DisplayName("출하 정보가 없으면 PO 수정 요청 시 예외가 발생한다")
    void validateModifiable_whenShipmentDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-0001";
        when(shipmentRepository.findByPoId(poId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderModificationService.validateModifiable(poId));
    }

    @Test
    @DisplayName("출하완료 상태인 PO는 삭제 요청 시 예외가 발생한다")
    void validateDeletable_whenShipmentCompleted_thenThrowsException() {
        // given
        String poId = "PO2025-0001";
        Shipment shipment = new Shipment(1L, poId, ShipmentStatus.COMPLETED);
        when(shipmentRepository.findByPoId(poId)).thenReturn(Optional.of(shipment));

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrderModificationService.validateDeletable(poId));
    }

    @Test
    @DisplayName("출하준비 상태인 PO는 삭제 요청 시 예외가 발생하지 않는다")
    void validateDeletable_whenShipmentReady_thenPasses() {
        // given
        String poId = "PO2025-0001";
        Shipment shipment = new Shipment(1L, poId, ShipmentStatus.READY);
        when(shipmentRepository.findByPoId(poId)).thenReturn(Optional.of(shipment));

        // when & then
        assertDoesNotThrow(() -> purchaseOrderModificationService.validateDeletable(poId));
    }

    @Test
    @DisplayName("출하 정보가 없으면 PO 삭제 요청 시 예외가 발생한다")
    void validateDeletable_whenShipmentDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-0001";
        when(shipmentRepository.findByPoId(poId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderModificationService.validateDeletable(poId));
    }
}
