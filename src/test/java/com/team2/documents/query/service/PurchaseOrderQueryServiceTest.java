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

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.query.mapper.PurchaseOrderQueryMapper;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderQueryServiceTest {

    @Mock
    private PurchaseOrderQueryMapper purchaseOrderQueryMapper;

    @InjectMocks
    private PurchaseOrderQueryService purchaseOrderQueryService;

    @Test
    @DisplayName("초기 상태 조회는 DRAFT를 반환한다")
    void determineInitialStatus_thenReturnsDraft() {
        PurchaseOrderStatus status = purchaseOrderQueryService.determineInitialStatus(1L);
        assertEquals(PurchaseOrderStatus.DRAFT, status);
    }

    @Test
    @DisplayName("PO ID로 조회 시 해당 PO를 반환한다")
    void findById_whenPurchaseOrderExists_thenReturnsPurchaseOrder() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT);
        when(purchaseOrderQueryMapper.findById("PO2025-0001")).thenReturn(purchaseOrder);

        // when
        PurchaseOrder result = purchaseOrderQueryService.findById("PO2025-0001");

        // then
        assertEquals("PO2025-0001", result.getPoId());
    }

    @Test
    @DisplayName("존재하지 않는 PO ID로 조회 시 예외를 던진다")
    void findById_whenPurchaseOrderNotExists_thenThrowsException() {
        // given
        when(purchaseOrderQueryMapper.findById("NOT-EXIST")).thenReturn(null);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderQueryService.findById("NOT-EXIST"));
    }

    @Test
    @DisplayName("전체 PO 목록을 조회한다")
    void findAll_whenPurchaseOrdersExist_thenReturnsAll() {
        // given
        PurchaseOrder purchaseOrder = new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT);
        when(purchaseOrderQueryMapper.findAll()).thenReturn(List.of(purchaseOrder));

        // when
        List<PurchaseOrder> result = purchaseOrderQueryService.findAll();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
