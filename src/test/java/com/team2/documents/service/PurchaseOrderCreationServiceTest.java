package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.UserPositionRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderCreationServiceTest {

    @Mock
    private UserPositionRepository userPositionRepository;

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @InjectMocks
    private PurchaseOrderCreationService purchaseOrderCreationService;

    @Test
    @DisplayName("팀장이 PO를 생성하면 초기 상태는 즉시 확정된다")
    void determineInitialStatus_whenManagerCreatesPurchaseOrder_thenConfirmed() {
        // given
        Long userId = 1L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));

        // when
        PurchaseOrderStatus status = purchaseOrderCreationService.determineInitialStatus(userId);

        // then
        assertEquals(PurchaseOrderStatus.CONFIRMED, status);
    }

    @Test
    @DisplayName("일반 직원이 PO를 생성하면 초기 상태는 결재대기다")
    void determineInitialStatus_whenStaffCreatesPurchaseOrder_thenApprovalPending() {
        // given
        Long userId = 2L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.STAFF));

        // when
        PurchaseOrderStatus status = purchaseOrderCreationService.determineInitialStatus(userId);

        // then
        assertEquals(PurchaseOrderStatus.APPROVAL_PENDING, status);
    }

    @Test
    @DisplayName("사용자 직급 정보가 없으면 PO 생성 시 예외가 발생한다")
    void determineInitialStatus_whenPositionLevelDoesNotExist_thenThrowsException() {
        // given
        Long userId = 3L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderCreationService.determineInitialStatus(userId));
    }

    @Test
    @DisplayName("일반 직원이 PO를 생성하면 결재 요청이 생성된다")
    void createPurchaseOrder_whenStaffCreates_thenCreatesApprovalRequest() {
        // given
        Long userId = 2L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.STAFF));

        // when
        purchaseOrderCreationService.create(userId);

        // then
        org.mockito.Mockito.verify(approvalRequestRepository).createForPurchaseOrder(userId);
    }

    @Test
    @DisplayName("팀장이 PO를 생성하면 결재 요청이 생성되지 않는다")
    void createPurchaseOrder_whenManagerCreates_thenDoesNotCreateApprovalRequest() {
        // given
        Long userId = 1L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));

        // when
        purchaseOrderCreationService.create(userId);

        // then
        org.mockito.Mockito.verify(approvalRequestRepository, org.mockito.Mockito.never())
                .createForPurchaseOrder(userId);
    }
}
