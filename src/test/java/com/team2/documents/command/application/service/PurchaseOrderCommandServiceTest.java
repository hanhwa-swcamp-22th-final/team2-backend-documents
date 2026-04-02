package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderCommandServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Test
    @DisplayName("PO ID로 조회 시 해당 PO를 반환한다")
    void findById_whenPurchaseOrderExists_thenReturnsPurchaseOrder() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT);
        when(purchaseOrderRepository.findByPoCode("PO2025-0001")).thenReturn(Optional.of(purchaseOrder));

        // when
        PurchaseOrder result = purchaseOrderCommandService.findById("PO2025-0001");

        // then
        assertEquals("PO2025-0001", result.getPoId());
    }

    @Test
    @DisplayName("존재하지 않는 PO ID로 조회 시 예외가 발생한다")
    void findById_whenPurchaseOrderDoesNotExist_thenThrowsException() {
        // given
        when(purchaseOrderRepository.findByPoCode("NOT-EXIST")).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderCommandService.findById("NOT-EXIST"));
    }

    @Test
    @DisplayName("PO 저장 시 저장된 PO를 반환한다")
    void save_whenPurchaseOrderIsValid_thenReturnsSavedPurchaseOrder() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT);
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);

        // when
        PurchaseOrder result = purchaseOrderCommandService.save(purchaseOrder);

        // then
        assertEquals("PO2025-0001", result.getPoId());
    }

    @Test
    @DisplayName("PO 상태 변경 시 상태가 변경된 PO를 반환한다")
    void updateStatus_whenPurchaseOrderExists_thenChangesStatus() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT);
        when(purchaseOrderRepository.findByPoCode("PO2025-0001")).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PurchaseOrder result = purchaseOrderCommandService.updateStatus("PO2025-0001", PurchaseOrderStatus.CONFIRMED);

        // then
        assertEquals(PurchaseOrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    @DisplayName("존재하지 않는 PO 상태 변경 시 예외가 발생한다")
    void updateStatus_whenPurchaseOrderDoesNotExist_thenThrowsException() {
        // given
        when(purchaseOrderRepository.findByPoCode("NOT-EXIST")).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderCommandService.updateStatus("NOT-EXIST", PurchaseOrderStatus.CONFIRMED));
    }
}
