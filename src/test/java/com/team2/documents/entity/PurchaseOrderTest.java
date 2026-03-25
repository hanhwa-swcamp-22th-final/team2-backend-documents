package com.team2.documents.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PurchaseOrderTest {

    @Test
    @DisplayName("출하준비 상태의 PO는 수정 가능하다")
    void validateModifiable_whenShipmentReady_thenPasses() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001");
        ShipmentStatus shipmentStatus = ShipmentStatus.READY;

        // when & then
        assertDoesNotThrow(() -> purchaseOrder.validateModifiable(shipmentStatus));
    }

    @Test
    @DisplayName("출하완료 상태의 PO는 수정할 수 없다")
    void validateModifiable_whenShipmentCompleted_thenThrowsException() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001");
        ShipmentStatus shipmentStatus = ShipmentStatus.COMPLETED;

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrder.validateModifiable(shipmentStatus));
    }

    @Test
    @DisplayName("출하완료 상태의 PO는 삭제할 수 없다")
    void validateDeletable_whenShipmentCompleted_thenThrowsException() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001");
        ShipmentStatus shipmentStatus = ShipmentStatus.COMPLETED;

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrder.validateDeletable(shipmentStatus));
    }
}
