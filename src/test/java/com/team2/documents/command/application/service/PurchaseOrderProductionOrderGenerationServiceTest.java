package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.application.service.ProductionOrderCommandService;
import com.team2.documents.command.application.service.PurchaseOrderCommandService;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderProductionOrderGenerationServiceTest {

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private ProductionOrderCommandService productionOrderCommandService;

    @Mock
    private DocumentNumberGeneratorService documentNumberGeneratorService;

    @Mock
    private DocumentLinkService documentLinkService;

    @Mock
    private DocumentRevisionHistoryService documentRevisionHistoryService;

    @Mock
    private DocumentAutoMailService documentAutoMailService;

    @InjectMocks
    private PurchaseOrderProductionOrderGenerationService purchaseOrderProductionOrderGenerationService;

    @Test
    @DisplayName("확정 상태의 PO는 생산지시서를 선택 생성할 수 있다")
    void generateProductionOrder_whenPurchaseOrderIsConfirmed_thenCreatesProductionOrder() {
        // given
        String poId = "PO2025-0001";
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.CONFIRMED);

        when(purchaseOrderCommandService.findById(poId)).thenReturn(purchaseOrder);
        when(documentNumberGeneratorService.nextProductionOrderId()).thenReturn("MO260001");

        // when
        purchaseOrderProductionOrderGenerationService.generate(poId);

        // then
        verify(productionOrderCommandService).save(any(com.team2.documents.command.domain.entity.ProductionOrder.class));
        verify(documentLinkService).linkProductionOrder(poId, "MO260001");
        verify(documentAutoMailService).sendProductionOrderToProductionTeam(
                org.mockito.ArgumentMatchers.eq(purchaseOrder),
                any(com.team2.documents.command.domain.entity.ProductionOrder.class)
        );
    }

    @Test
    @DisplayName("PO 정보가 없으면 생산지시서 선택 생성 시 예외가 발생한다")
    void generateProductionOrder_whenPurchaseOrderDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-9999";
        when(purchaseOrderCommandService.findById(poId))
                .thenThrow(new IllegalArgumentException("PO 정보를 찾을 수 없습니다."));

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

        when(purchaseOrderCommandService.findById(poId)).thenReturn(purchaseOrder);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> purchaseOrderProductionOrderGenerationService.generate(poId));
    }
}
