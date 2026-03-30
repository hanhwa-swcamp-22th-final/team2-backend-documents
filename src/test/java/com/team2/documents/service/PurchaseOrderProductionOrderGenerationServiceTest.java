package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.repository.ProductionOrderRepository;
import com.team2.documents.repository.PurchaseOrderRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderProductionOrderGenerationServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ProductionOrderRepository productionOrderRepository;

    @InjectMocks
    private PurchaseOrderProductionOrderGenerationService purchaseOrderProductionOrderGenerationService;

    @Test
    @DisplayName("확정 상태의 PO는 생산지시서를 선택 생성할 수 있다")
    void generateProductionOrder_whenPurchaseOrderIsConfirmed_thenCreatesProductionOrder() {
        // given
        String poId = "PO2025-0001";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.CONFIRMED);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));

        // when
        purchaseOrderProductionOrderGenerationService.generate(poId);

        // then
        verify(productionOrderRepository).save(any(com.team2.documents.entity.ProductionOrder.class));
    }

    @Test
    @DisplayName("PO 정보가 없으면 생산지시서 선택 생성 시 예외가 발생한다")
    void generateProductionOrder_whenPurchaseOrderDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-9999";
        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderProductionOrderGenerationService.generate(poId));
    }

    @Test
    @DisplayName("확정 상태가 아닌 PO는 생산지시서를 선택 생성할 수 없다")
    void generateProductionOrder_whenPurchaseOrderIsNotConfirmed_thenThrowsException() {
        // given
        String poId = "PO2025-0001";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.APPROVAL_PENDING);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrderProductionOrderGenerationService.generate(poId));
    }
}
