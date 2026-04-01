package com.team2.documents.command.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.dto.PurchaseOrderCreateRequest;
import com.team2.documents.dto.PurchaseOrderItemCreateRequest;
import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.PurchaseOrder;
import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.repository.UserPositionRepository;
import com.team2.documents.command.service.ApprovalRequestCommandService;
import com.team2.documents.command.service.PurchaseOrderCommandService;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderCreationServiceTest {

    @Mock
    private UserPositionRepository userPositionRepository;

    @Mock
    private ApprovalRequestCommandService approvalRequestCommandService;

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private ObjectMapper objectMapper;

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
        stubJsonSerialization();
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        purchaseOrderCreationService.create(userId);

        // then
        verify(purchaseOrderCommandService).save(argThat(purchaseOrder ->
                PurchaseOrderStatus.APPROVAL_PENDING.equals(purchaseOrder.getStatus())));
        verify(approvalRequestCommandService).save(any(ApprovalRequest.class));
    }

    @Test
    @DisplayName("팀장이 PO를 생성하면 결재 요청이 생성되지 않는다")
    void createPurchaseOrder_whenManagerCreates_thenDoesNotCreateApprovalRequest() {
        // given
        Long userId = 1L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));
        stubJsonSerialization();
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        purchaseOrderCreationService.create(userId);

        // then
        verify(purchaseOrderCommandService).save(argThat(purchaseOrder ->
                PurchaseOrderStatus.CONFIRMED.equals(purchaseOrder.getStatus())));
        verify(approvalRequestCommandService, never()).save(any(ApprovalRequest.class));
    }

    @Test
    @DisplayName("PO 생성 시 품목 스냅샷과 연결문서, 이력이 함께 저장된다")
    void createPurchaseOrder_whenRequestContainsItems_thenStoresSnapshotMetadata() throws JsonProcessingException {
        // given
        Long userId = 2L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.STAFF));
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("[{\"itemName\":\"Bolt\"}]", "[]", "[{\"action\":\"CREATE\"}]");
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                "PO2026-0001",
                "PI2026-0001",
                LocalDate.of(2026, 4, 1),
                10,
                1,
                2L,
                LocalDate.of(2026, 4, 15),
                "FOB",
                "Busan",
                LocalDate.of(2026, 4, 10),
                true,
                null,
                "ABC Trading",
                "Seoul",
                "KR",
                "USD",
                "Kim",
                userId,
                List.of(new PurchaseOrderItemCreateRequest(
                        100,
                        "Bolt",
                        3,
                        "EA",
                        new BigDecimal("10.00"),
                        null,
                        "urgent"
                ))
        );

        // when
        PurchaseOrder created = purchaseOrderCreationService.create(request);

        // then
        assertEquals(new BigDecimal("30.00"), created.getTotalAmount());
        assertEquals(1, created.getItems().size());
        assertEquals(new BigDecimal("30.00"), created.getItems().get(0).getAmount());
        assertEquals("[{\"itemName\":\"Bolt\"}]", created.getItemsSnapshot());
        assertEquals("[]", created.getLinkedDocuments());
        assertEquals("[{\"action\":\"CREATE\"}]", created.getRevisionHistory());
        assertTrue(created.getApprovalRequestedAt() != null);
    }

    private void stubJsonSerialization() {
        try {
            when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
