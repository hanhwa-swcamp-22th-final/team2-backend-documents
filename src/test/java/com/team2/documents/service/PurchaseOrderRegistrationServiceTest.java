package com.team2.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.entity.PositionLevel;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.PurchaseOrderStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.PurchaseOrderRepository;
import com.team2.documents.repository.UserPositionRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderRegistrationServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private UserPositionRepository userPositionRepository;

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @InjectMocks
    private PurchaseOrderRegistrationService purchaseOrderRegistrationService;

    @Test
    @DisplayName("일반 직원이 PO 등록 요청을 하면 상태를 결재대기로 바꾸고 결재 요청을 생성한다")
    void requestRegistration_whenStaffRequests_thenChangesStatusAndCreatesApprovalRequest() {
        // given
        String poId = "PO2025-0001";
        Long userId = 2L;
        PurchaseOrder purchaseOrder = new PurchaseOrder(poId, PurchaseOrderStatus.DRAFT);

        when(purchaseOrderRepository.findById(poId)).thenReturn(Optional.of(purchaseOrder));
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.STAFF));

        // when
        purchaseOrderRegistrationService.requestRegistration(poId, userId);

        // then
        assertEquals(PurchaseOrderStatus.APPROVAL_PENDING, purchaseOrder.getStatus());
        verify(approvalRequestRepository).createForPurchaseOrder(userId);
    }
}
