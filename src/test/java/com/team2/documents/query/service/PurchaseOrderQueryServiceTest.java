package com.team2.documents.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.UserPositionRepository;
import com.team2.documents.query.mapper.PurchaseOrderQueryMapper;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderQueryServiceTest {

    @Mock
    private PurchaseOrderQueryMapper purchaseOrderQueryMapper;

    @Mock
    private UserPositionRepository userPositionRepository;

    @InjectMocks
    private PurchaseOrderQueryService purchaseOrderQueryService;

    @Test
    @DisplayName("매니저 직급의 사용자인 경우 CONFIRMED 상태를 반환한다")
    void determineInitialStatus_whenManager_thenReturnsConfirmed() {
        // given
        when(userPositionRepository.findPositionLevelByUserId(1L))
                .thenReturn(Optional.of(PositionLevel.MANAGER));

        // when
        PurchaseOrderStatus status = purchaseOrderQueryService.determineInitialStatus(1L);

        // then
        assertEquals(PurchaseOrderStatus.CONFIRMED, status);
    }

    @Test
    @DisplayName("일반 직원 직급의 사용자인 경우 APPROVAL_PENDING 상태를 반환한다")
    void determineInitialStatus_whenStaff_thenReturnsApprovalPending() {
        // given
        when(userPositionRepository.findPositionLevelByUserId(2L))
                .thenReturn(Optional.of(PositionLevel.STAFF));

        // when
        PurchaseOrderStatus status = purchaseOrderQueryService.determineInitialStatus(2L);

        // then
        assertEquals(PurchaseOrderStatus.APPROVAL_PENDING, status);
    }

    @Test
    @DisplayName("사용자 직급 정보가 없으면 예외를 던진다")
    void determineInitialStatus_whenPositionNotFound_thenThrowsException() {
        // given
        when(userPositionRepository.findPositionLevelByUserId(99L))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderQueryService.determineInitialStatus(99L));
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
