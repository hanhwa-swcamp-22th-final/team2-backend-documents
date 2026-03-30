package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.PurchaseOrderRepository;
import com.team2.documents.repository.UserPositionRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderModificationRequestServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private UserPositionRepository userPositionRepository;

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @InjectMocks
    private PurchaseOrderModificationRequestService purchaseOrderModificationRequestService;

    @Test
    @DisplayName("일반 직원이 PO 수정 요청을 하면 상태를 결재대기로 바꾸고 결재 요청을 생성한다")
    void requestModification_whenStaffRequests_thenChangesStatusAndCreatesApprovalRequest() {
        // given
        String poId = "PO2025-0001";
        Long userId = 2L;
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.CONFIRMED);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.STAFF));

        // when
        purchaseOrderModificationRequestService.requestModification(poId, userId);

        // then
        assertEquals(PurchaseOrderStatus.APPROVAL_PENDING, purchaseOrder.getStatus());
        verify(approvalRequestRepository).save(any(com.team2.documents.entity.ApprovalRequest.class));
    }

    @Test
    @DisplayName("팀장이 PO 수정 요청을 하면 결재 요청이 생성되지 않는다")
    void requestModification_whenManagerRequests_thenDoesNotCreateApprovalRequest() {
        // given
        String poId = "PO2025-0001";
        Long userId = 1L;
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.CONFIRMED);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));

        // when
        purchaseOrderModificationRequestService.requestModification(poId, userId);

        // then
        assertEquals(PurchaseOrderStatus.APPROVAL_PENDING, purchaseOrder.getStatus());
        org.mockito.Mockito.verify(approvalRequestRepository, org.mockito.Mockito.never())
                .save(any(com.team2.documents.entity.ApprovalRequest.class));
    }

    @Test
    @DisplayName("PO 정보가 없으면 수정 요청 시 예외가 발생한다")
    void requestModification_whenPurchaseOrderDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-9999";
        Long userId = 2L;
        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderModificationRequestService.requestModification(poId, userId));
    }

    @Test
    @DisplayName("사용자 직급 정보가 없으면 수정 요청 시 예외가 발생한다")
    void requestModification_whenPositionLevelDoesNotExist_thenThrowsException() {
        // given
        String poId = "PO2025-0001";
        Long userId = 99L;
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.CONFIRMED);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderModificationRequestService.requestModification(poId, userId));
    }

    @Test
    @DisplayName("확정 상태가 아닌 PO는 수정 요청할 수 없다")
    void requestModification_whenPurchaseOrderIsNotConfirmed_thenThrowsException() {
        // given
        String poId = "PO2025-0002";
        Long userId = 2L;
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.APPROVAL_PENDING);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.STAFF));

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> purchaseOrderModificationRequestService.requestModification(poId, userId));
    }
}
