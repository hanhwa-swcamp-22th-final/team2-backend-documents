package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import com.team2.documents.command.application.service.ShipmentCommandService;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderModificationServiceTest {

    @Mock
    private ShipmentCommandService shipmentCommandService;

    @InjectMocks
    private PurchaseOrderModificationService purchaseOrderModificationService;

    @Test
    @DisplayName("출하준비 상태인 PO는 수정 요청 시 예외가 발생하지 않는다")
    void validateModifiable_whenShipmentReady_thenPasses() {
        // given
        String poId = "PO2025-0001";
        Shipment shipment = new Shipment(1L, poId, ShipmentStatus.READY);
        when(shipmentCommandService.findByPoId(poId)).thenReturn(shipment);

        // when & then
        assertDoesNotThrow(() -> purchaseOrderModificationService.validateModifiable(poId));
    }

    @Test
    @DisplayName("출하완료 상태인 PO는 수정 요청 시 예외가 발생한다")
    void validateModifiable_whenShipmentCompleted_thenThrowsException() {
        // given
        String poId = "PO2025-0001";
        Shipment shipment = new Shipment(1L, poId, ShipmentStatus.COMPLETED);
        when(shipmentCommandService.findByPoId(poId)).thenReturn(shipment);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrderModificationService.validateModifiable(poId));
    }

    @Test
    @DisplayName("출하 정보가 없으면 PO 수정 요청 시 예외가 발생한다")
    void validateModifiable_whenShipmentDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-0001";
        when(shipmentCommandService.findByPoId(poId))
                .thenThrow(new IllegalArgumentException("해당 PO의 출하현황 정보를 찾을 수 없습니다."));

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
        when(shipmentCommandService.findByPoId(poId)).thenReturn(shipment);

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
        when(shipmentCommandService.findByPoId(poId)).thenReturn(shipment);

        // when & then
        assertDoesNotThrow(() -> purchaseOrderModificationService.validateDeletable(poId));
    }

    @Test
    @DisplayName("출하 정보가 없으면 PO 삭제 요청 시 예외가 발생한다")
    void validateDeletable_whenShipmentDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-0001";
        when(shipmentCommandService.findByPoId(poId))
                .thenThrow(new IllegalArgumentException("해당 PO의 출하현황 정보를 찾을 수 없습니다."));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderModificationService.validateDeletable(poId));
    }
}
