package com.team2.documents.command.application.service;

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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.PurchaseOrderCreateRequest;
import com.team2.documents.command.application.dto.PurchaseOrderItemCreateRequest;
import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.UserPositionRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PurchaseOrderCreationServiceTest {

    @Mock
    private UserPositionRepository userPositionRepository;

    @Mock
    private ApprovalRequestCommandService approvalRequestCommandService;

    @Mock
    private PurchaseOrderCommandService purchaseOrderCommandService;

    @Mock
    private DocumentNumberGeneratorService documentNumberGeneratorService;

    @Mock
    private DocumentLinkService documentLinkService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PurchaseOrderCreationService purchaseOrderCreationService;

    @Test
    @DisplayName("PO 생성 초기 상태는 DRAFT다")
    void determineInitialStatus_whenCreatePurchaseOrder_thenDraft() {
        PurchaseOrderStatus status = purchaseOrderCreationService.determineInitialStatus(1L);
        assertEquals(PurchaseOrderStatus.DRAFT, status);
    }

    @Test
    @DisplayName("PO 생성은 초안만 저장하고 결재 요청을 즉시 만들지 않는다")
    void createPurchaseOrder_whenCreated_thenStoresDraft() {
        Long userId = 2L;
        stubJsonSerialization();
        when(documentNumberGeneratorService.nextPurchaseOrderId()).thenReturn("PO260001");
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        purchaseOrderCreationService.create(userId);

        verify(purchaseOrderCommandService).save(argThat(purchaseOrder ->
                PurchaseOrderStatus.DRAFT.equals(purchaseOrder.getStatus())));
        verify(approvalRequestCommandService, never()).save(any(ApprovalRequest.class));
    }

    @Test
    @DisplayName("PO 생성 시 품목 스냅샷과 연결문서, 이력이 함께 저장된다")
    void createPurchaseOrder_whenRequestContainsItems_thenStoresSnapshotMetadata() throws JsonProcessingException {
        // given
        Long userId = 2L;
        when(documentNumberGeneratorService.nextPurchaseOrderId()).thenReturn("PO260001");
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
        assertEquals(PurchaseOrderStatus.DRAFT, created.getStatus());
        assertTrue(created.getApprovalRequestedAt() == null);
        verify(documentLinkService).linkPurchaseOrderToProformaInvoice("PO2026-0001", "PI2026-0001");
    }

    @Test
    @DisplayName("PO 생성 시 요청 필드가 null이면 기본값이 적용된다")
    void createPurchaseOrder_whenFieldsAreNull_thenDefaultsApplied() throws JsonProcessingException {
        // given
        Long userId = 1L;
        when(documentNumberGeneratorService.nextPurchaseOrderId()).thenReturn("PO260001");
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // null poId, null issueDate, null clientId, null currencyId, null managerId, null items
        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                null, null, null, null, null, null,
                null, null, null, null, null,
                BigDecimal.TEN, null, null, null, null, null, userId, null
        );

        // when
        PurchaseOrder created = purchaseOrderCreationService.create(request);

        // then
        assertEquals("PO260001", created.getPoId());
        assertEquals(LocalDate.now(), created.getIssueDate());
        assertEquals(0, created.getClientId());
        assertEquals(0, created.getCurrencyId());
        assertEquals(BigDecimal.TEN, created.getTotalAmount());
        assertTrue(created.getItems().isEmpty());
    }

    @Test
    @DisplayName("PO 생성 시 품목의 amount가 null이면 unitPrice * quantity로 계산된다")
    void createPurchaseOrder_whenItemAmountNull_thenCalculated() throws JsonProcessingException {
        // given
        Long userId = 1L;
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // item with null amount, null unitPrice, null quantity
        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                "PO-001", null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null, userId,
                List.of(
                        new PurchaseOrderItemCreateRequest(1, null, null, null, null, null, null)
                )
        );

        // when
        PurchaseOrder created = purchaseOrderCreationService.create(request);

        // then
        assertEquals(1, created.getItems().size());
        assertEquals(BigDecimal.ZERO, created.getItems().get(0).getAmount());
        assertEquals("", created.getItems().get(0).getItemName());
    }

    @Test
    @DisplayName("PO 생성 시 managerId가 있으면 managerId가 사용된다")
    void createPurchaseOrder_whenManagerIdProvided_thenUsed() throws JsonProcessingException {
        // given
        Long userId = 1L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                "PO-001", null, null, null, null, 99L,
                null, null, null, null, null,
                null, null, null, null, null, null, userId, null
        );

        // when
        PurchaseOrder created = purchaseOrderCreationService.create(request);

        // then
        assertEquals(99L, created.getManagerId());
    }

    @Test
    @DisplayName("PO 생성 시 품목에 amount, unit, remark, itemId가 있으면 그대로 사용된다")
    void createPurchaseOrder_whenItemFieldsProvided_thenUsed() throws JsonProcessingException {
        // given
        Long userId = 1L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                "PO-001", null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null, userId,
                List.of(
                        new PurchaseOrderItemCreateRequest(100, "Bolt", 5, "EA",
                                new BigDecimal("10.00"), new BigDecimal("50.00"), "note")
                )
        );

        // when
        PurchaseOrder created = purchaseOrderCreationService.create(request);

        // then
        assertEquals(1, created.getItems().size());
        assertEquals(new BigDecimal("50.00"), created.getItems().get(0).getAmount());
        assertEquals("Bolt", created.getItems().get(0).getItemName());
    }

    @Test
    @DisplayName("PO 생성 시 품목의 itemId, unit, remark가 null이면 스냅샷에서 기본값이 적용된다")
    void createPurchaseOrder_whenItemSnapshotFieldsNull_thenDefaultsApplied() throws JsonProcessingException {
        // given
        Long userId = 1L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // item with null itemId, non-null itemName, non-null quantity/unitPrice, null unit, null remark
        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                "PO-SNAP", null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null, userId,
                List.of(
                        new PurchaseOrderItemCreateRequest(null, "Widget", 2, null,
                                new BigDecimal("5.00"), null, null)
                )
        );

        // when
        PurchaseOrder created = purchaseOrderCreationService.create(request);

        // then
        assertEquals(1, created.getItems().size());
        assertEquals(new BigDecimal("10.00"), created.getItems().get(0).getAmount());
    }

    @Test
    @DisplayName("PO 생성 시 poId가 빈 문자열이면 TEMP-PO 형식으로 생성된다")
    void createPurchaseOrder_whenPoIdIsBlank_thenTempPoIdGenerated() throws JsonProcessingException {
        // given
        Long userId = 1L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // poId is blank (not null, but empty)
        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                "", null, null, null, null, null,
                null, null, null, null, null,
                BigDecimal.ZERO, null, null, null, null, null, userId, null
        );
        when(documentNumberGeneratorService.nextPurchaseOrderId()).thenReturn("PO260001");

        // when
        PurchaseOrder created = purchaseOrderCreationService.create(request);

        // then
        assertEquals("PO260001", created.getPoId());
    }

    @Test
    @DisplayName("PO 생성 시 managerId와 userId 모두 null이면 managerId는 0L이 된다")
    void createPurchaseOrder_whenManagerIdAndUserIdBothNull_thenManagerIdIsZero() throws JsonProcessingException {
        // given
        when(userPositionRepository.findPositionLevelByUserId(null))
                .thenReturn(Optional.of(PositionLevel.MANAGER));
        when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        when(purchaseOrderCommandService.save(any(PurchaseOrder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // managerId = null, userId = null
        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                "PO-NULL", null, null, null, null, null,
                null, null, null, null, null,
                BigDecimal.ZERO, null, null, null, null, null, null, null
        );

        // when
        PurchaseOrder created = purchaseOrderCreationService.create(request);

        // then
        assertEquals(0L, created.getManagerId());
    }

    @Test
    @DisplayName("JSON 직렬화 실패 시 IllegalStateException이 발생한다")
    void createPurchaseOrder_whenJsonSerializationFails_thenThrows() throws JsonProcessingException {
        // given
        Long userId = 1L;
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("error") {});

        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                "PO-001", null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null, userId, null
        );

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> purchaseOrderCreationService.create(request));
    }

    private void stubJsonSerialization() {
        try {
            when(objectMapper.writeValueAsString(any())).thenReturn("[]");
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
