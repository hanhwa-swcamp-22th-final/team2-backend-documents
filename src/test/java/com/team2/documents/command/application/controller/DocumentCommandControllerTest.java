package com.team2.documents.command.application.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.ApprovalRequestCreateRequest;
import com.team2.documents.command.application.dto.ApprovalRequestUpdateRequest;
import com.team2.documents.command.application.dto.ProformaInvoiceCreateRequest;
import com.team2.documents.command.application.dto.ProformaInvoiceRegistrationRequest;
import com.team2.documents.command.application.dto.PurchaseOrderDeletionRequest;
import com.team2.documents.command.application.dto.PurchaseOrderModificationRequest;
import com.team2.documents.command.application.dto.PurchaseOrderRegistrationRequest;
import com.team2.documents.command.application.dto.CollectionUpdateRequest;
import com.team2.documents.command.application.dto.ShipmentStatusUpdateRequest;
import com.team2.documents.command.domain.entity.Collection;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.ApprovalRequest;
import com.team2.documents.command.domain.entity.CommercialInvoice;
import com.team2.documents.command.domain.entity.PackingList;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import com.team2.documents.command.application.service.ApprovalRequestCommandService;
import com.team2.documents.command.application.service.CollectionCommandService;
import com.team2.documents.command.application.service.ProductionOrderCommandService;
import com.team2.documents.command.application.service.ShipmentCommandService;
import com.team2.documents.query.service.ApprovalRequestQueryService;
import com.team2.documents.query.service.CommercialInvoiceQueryService;
import com.team2.documents.query.service.CollectionQueryService;
import com.team2.documents.query.service.PackingListQueryService;
import com.team2.documents.query.service.ProductionOrderQueryService;
import com.team2.documents.query.service.ProformaInvoiceQueryService;
import com.team2.documents.query.service.ShipmentOrderQueryService;
import com.team2.documents.query.service.ShipmentQueryService;
import com.team2.documents.command.application.service.ProformaInvoiceApprovalWorkflowService;
import com.team2.documents.command.application.service.ProformaInvoiceCreationService;
import com.team2.documents.command.application.service.ProformaInvoiceRejectionWorkflowService;
import com.team2.documents.command.application.service.ProformaInvoiceService;
import com.team2.documents.command.application.service.PurchaseOrderApprovalWorkflowService;
import com.team2.documents.command.application.service.PurchaseOrderCreationService;
import com.team2.documents.command.application.service.PurchaseOrderDocumentGenerationService;
import com.team2.documents.command.application.service.PurchaseOrderProductionOrderGenerationService;
import com.team2.documents.command.application.service.PurchaseOrderDeletionRequestService;
import com.team2.documents.command.application.service.PurchaseOrderModificationService;
import com.team2.documents.command.application.service.PurchaseOrderModificationRequestService;
import com.team2.documents.command.application.service.PurchaseOrderRejectionWorkflowService;
import com.team2.documents.command.application.service.PurchaseOrderRegistrationService;

@WebMvcTest({DocumentCommandController.class, com.team2.documents.query.controller.DocumentQueryController.class})
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PurchaseOrderModificationService purchaseOrderModificationService;

    @MockitoBean
    private ProformaInvoiceApprovalWorkflowService proformaInvoiceApprovalWorkflowService;

    @MockitoBean
    private ProformaInvoiceRejectionWorkflowService proformaInvoiceRejectionWorkflowService;

    @MockitoBean
    private PurchaseOrderApprovalWorkflowService purchaseOrderApprovalWorkflowService;

    @MockitoBean
    private PurchaseOrderRejectionWorkflowService purchaseOrderRejectionWorkflowService;

    @MockitoBean
    private PurchaseOrderModificationRequestService purchaseOrderModificationRequestService;

    @MockitoBean
    private PurchaseOrderDeletionRequestService purchaseOrderDeletionRequestService;

    @MockitoBean
    private PurchaseOrderCreationService purchaseOrderCreationService;

    @MockitoBean
    private PurchaseOrderDocumentGenerationService purchaseOrderDocumentGenerationService;

    @MockitoBean
    private PurchaseOrderProductionOrderGenerationService purchaseOrderProductionOrderGenerationService;

    @MockitoBean
    private PurchaseOrderRegistrationService purchaseOrderRegistrationService;

    @MockitoBean
    private ProformaInvoiceCreationService proformaInvoiceCreationService;

    @MockitoBean
    private ProformaInvoiceService proformaInvoiceService;

    @MockitoBean
    private ProductionOrderCommandService productionOrderCommandService;

    @MockitoBean
    private ProductionOrderQueryService productionOrderQueryService;

    @MockitoBean
    private ShipmentCommandService shipmentCommandService;

    @MockitoBean
    private ShipmentQueryService shipmentQueryService;

    @MockitoBean
    private CollectionCommandService collectionCommandService;

    @MockitoBean
    private CollectionQueryService collectionQueryService;

    @MockitoBean
    private com.team2.documents.query.service.PurchaseOrderQueryService purchaseOrderQueryService;

    @MockitoBean
    private ProformaInvoiceQueryService proformaInvoiceQueryService;

    @MockitoBean
    private CommercialInvoiceQueryService commercialInvoiceQueryService;

    @MockitoBean
    private PackingListQueryService packingListQueryService;

    @MockitoBean
    private ShipmentOrderQueryService shipmentOrderQueryService;

    @MockitoBean
    private ApprovalRequestQueryService approvalRequestQueryService;

    @MockitoBean
    private ApprovalRequestCommandService approvalRequestCommandService;

    @Test
    @DisplayName("PO 생성 API 호출 시 200 OK를 반환하고 Service를 호출한다")
    void createApi_whenRequestIsValid_thenReturnsOkAndCallsService() throws Exception {
        // given
        Long userId = 2L;
        when(purchaseOrderCreationService.create(org.mockito.ArgumentMatchers.any(
                com.team2.documents.command.application.dto.PurchaseOrderCreateRequest.class)))
                .thenReturn(new PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT));

        // when & then
        mockMvc.perform(post("/api/purchase-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new com.team2.documents.command.application.dto.PurchaseOrderCreateRequest(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PO 생성 요청이 처리되었습니다."));

        verify(purchaseOrderCreationService).create(org.mockito.ArgumentMatchers.any(
                com.team2.documents.command.application.dto.PurchaseOrderCreateRequest.class));
    }

    @Test
    @DisplayName("PI 등록 요청 API 호출 시 200 OK를 반환하고 Service를 호출한다")
    void requestRegistrationApi_whenRequestIsValid_thenReturnsOkAndCallsService() throws Exception {
        // given
        String piId = "PI2025-0001";
        Long userId = 2L;
        doNothing().when(proformaInvoiceService).requestRegistration(piId, userId);

        // when & then
        mockMvc.perform(post("/api/proforma-invoices/request-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ProformaInvoiceRegistrationRequest(piId, userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PI 등록 요청이 처리되었습니다."));

        verify(proformaInvoiceService).requestRegistration(piId, userId);
    }

    @Test
    @DisplayName("PI 생성 API 호출 시 200 OK를 반환하고 Service를 호출한다")
    void createProformaInvoiceApi_whenRequestIsValid_thenReturnsOkAndCallsService() throws Exception {
        ProformaInvoiceCreateRequest request = new ProformaInvoiceCreateRequest(
                "PI260001", null, null, null, null, null, null, null,
                null, null, null, null, null, null, 2L, java.util.List.of()
        );
        when(proformaInvoiceCreationService.create(org.mockito.ArgumentMatchers.any(ProformaInvoiceCreateRequest.class)))
                .thenReturn(new ProformaInvoice("PI260001", com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus.DRAFT));

        mockMvc.perform(post("/api/proforma-invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PI 생성 요청이 처리되었습니다."))
                .andExpect(jsonPath("$.piId").value("PI260001"));

        verify(proformaInvoiceCreationService).create(org.mockito.ArgumentMatchers.any(ProformaInvoiceCreateRequest.class));
    }

    @Test
    @DisplayName("PO 등록 요청 API 호출 시 200 OK를 반환하고 Service를 호출한다")
    void requestPurchaseOrderRegistrationApi_whenRequestIsValid_thenReturnsOkAndCallsService() throws Exception {
        // given
        String poId = "PO2025-0001";
        Long userId = 2L;
        doNothing().when(purchaseOrderRegistrationService).requestRegistration(poId, userId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders/request-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new PurchaseOrderRegistrationRequest(poId, userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PO 등록 요청이 처리되었습니다."));

        verify(purchaseOrderRegistrationService).requestRegistration(poId, userId);
    }

    @Test
    @DisplayName("PO 수정 요청 API 호출 시 200 OK를 반환하고 Service를 호출한다")
    void requestPurchaseOrderModificationApi_whenRequestIsValid_thenReturnsOkAndCallsService() throws Exception {
        // given
        String poId = "PO2025-0001";
        Long userId = 2L;
        doNothing().when(purchaseOrderModificationRequestService).requestModification(poId, userId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders/request-modification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new PurchaseOrderModificationRequest(poId, userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PO 수정 요청이 처리되었습니다."));

        verify(purchaseOrderModificationRequestService).requestModification(poId, userId);
    }

    @Test
    @DisplayName("PO 삭제 요청 API 호출 시 200 OK를 반환하고 Service를 호출한다")
    void requestPurchaseOrderDeletionApi_whenRequestIsValid_thenReturnsOkAndCallsService() throws Exception {
        // given
        String poId = "PO2025-0001";
        Long userId = 2L;
        doNothing().when(purchaseOrderDeletionRequestService).requestDeletion(poId, userId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders/request-deletion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new PurchaseOrderDeletionRequest(poId, userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PO 삭제 요청이 처리되었습니다."));

        verify(purchaseOrderDeletionRequestService).requestDeletion(poId, userId);
    }

    @Test
    @DisplayName("PO 단건 조회 API 호출 시 200 OK와 PO를 반환한다")
    void getPurchaseOrderApi_whenPurchaseOrderExists_thenReturnsOkAndPurchaseOrder() throws Exception {
        // given
        com.team2.documents.command.domain.entity.PurchaseOrder purchaseOrder =
                new com.team2.documents.command.domain.entity.PurchaseOrder("PO2025-0001", PurchaseOrderStatus.DRAFT);
        when(purchaseOrderQueryService.findById("PO2025-0001")).thenReturn(purchaseOrder);

        // when & then
        mockMvc.perform(get("/api/purchase-orders/{poId}", "PO2025-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poId").value("PO2025-0001"))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(purchaseOrderQueryService).findById("PO2025-0001");
    }

    @Test
    @DisplayName("PO 초기 상태 조회 API 호출 시 200 OK와 상태값을 반환한다")
    void determineInitialStatusApi_whenRequestIsValid_thenReturnsOkAndStatus() throws Exception {
        // given
        Long userId = 1L;
        when(purchaseOrderQueryService.determineInitialStatus(userId))
                .thenReturn(PurchaseOrderStatus.DRAFT);

        // when & then
        mockMvc.perform(get("/api/purchase-orders/initial-status/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));

        verify(purchaseOrderQueryService).determineInitialStatus(userId);
    }

    @Test
    @DisplayName("생산지시서 목록 조회 API 호출 시 200 OK와 목록을 반환한다")
    void getProductionOrdersApi_whenProductionOrdersExist_thenReturnsOkAndList() throws Exception {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                "PRD-2026-001",
                "PO2025001",
                "PO-2026-001",
                java.time.LocalDate.of(2026, 3, 10),
                java.time.LocalDate.of(2026, 4, 10),
                "진행중",
                java.util.List.of(),
                java.time.LocalDateTime.of(2026, 3, 10, 9, 0),
                java.time.LocalDateTime.of(2026, 3, 15, 14, 0)
        );
        when(productionOrderQueryService.findAll()).thenReturn(java.util.List.of(productionOrder));

        // when & then
        mockMvc.perform(get("/api/production-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productionOrderId").value("PRD-2026-001"))
                .andExpect(jsonPath("$[0].poId").value("PO2025001"))
                .andExpect(jsonPath("$[0].status").value("진행중"));

        verify(productionOrderQueryService).findAll();
    }

    @Test
    @DisplayName("생산지시서 단건 조회 API 호출 시 200 OK와 생산지시서를 반환한다")
    void getProductionOrderApi_whenProductionOrderExists_thenReturnsOkAndProductionOrder() throws Exception {
        // given
        ProductionOrder productionOrder = new ProductionOrder(
                "PRD-2026-001",
                "PO2025001",
                "PO-2026-001",
                java.time.LocalDate.of(2026, 3, 10),
                java.time.LocalDate.of(2026, 4, 10),
                "진행중",
                java.util.List.of(),
                java.time.LocalDateTime.of(2026, 3, 10, 9, 0),
                java.time.LocalDateTime.of(2026, 3, 15, 14, 0)
        );
        when(productionOrderQueryService.findById("PRD-2026-001")).thenReturn(productionOrder);

        // when & then
        mockMvc.perform(get("/api/production-orders/{id}", "PRD-2026-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productionOrderId").value("PRD-2026-001"))
                .andExpect(jsonPath("$.poId").value("PO2025001"))
                .andExpect(jsonPath("$.status").value("진행중"));

        verify(productionOrderQueryService).findById("PRD-2026-001");
    }

    @Test
    @DisplayName("출하현황 목록 조회 API 호출 시 200 OK와 목록을 반환한다")
    void getShipmentsApi_whenShipmentsExist_thenReturnsOkAndList() throws Exception {
        // given
        com.team2.documents.command.domain.entity.Shipment shipment =
                new com.team2.documents.command.domain.entity.Shipment(1L, "PO2025-0001", com.team2.documents.command.domain.entity.enums.ShipmentStatus.READY);
        when(shipmentQueryService.findAll()).thenReturn(java.util.List.of(shipment));

        // when & then
        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shipmentId").value(1))
                .andExpect(jsonPath("$[0].poId").value("PO2025-0001"))
                .andExpect(jsonPath("$[0].shipmentStatus").value("READY"));

        verify(shipmentQueryService).findAll();
    }

    @Test
    @DisplayName("출하현황 단건 조회 API 호출 시 200 OK와 출하현황을 반환한다")
    void getShipmentApi_whenShipmentExists_thenReturnsOkAndShipment() throws Exception {
        // given
        com.team2.documents.command.domain.entity.Shipment shipment =
                new com.team2.documents.command.domain.entity.Shipment(1L, "PO2025-0001", com.team2.documents.command.domain.entity.enums.ShipmentStatus.READY);
        when(shipmentQueryService.findById(1L)).thenReturn(shipment);

        // when & then
        mockMvc.perform(get("/api/shipments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentId").value(1))
                .andExpect(jsonPath("$.poId").value("PO2025-0001"))
                .andExpect(jsonPath("$.shipmentStatus").value("READY"));

        verify(shipmentQueryService).findById(1L);
    }

    @Test
    @DisplayName("매출·수금 현황 목록 조회 API 호출 시 200 OK와 목록을 반환한다")
    void getCollectionsApi_whenCollectionsExist_thenReturnsOkAndList() throws Exception {
        // given
        Collection collection = new Collection(
                1L,
                "PO2025001",
                "PO-2026-001",
                1L,
                "ABC Trading",
                new java.math.BigDecimal("15000.00"),
                new java.math.BigDecimal("10000.00"),
                new java.math.BigDecimal("5000.00"),
                "USD",
                "미수금",
                java.time.LocalDate.of(2026, 5, 15),
                java.time.LocalDateTime.of(2026, 3, 5, 9, 0),
                java.time.LocalDateTime.of(2026, 5, 15, 14, 0)
        );
        when(collectionQueryService.findAll()).thenReturn(java.util.List.of(collection));

        // when & then
        mockMvc.perform(get("/api/collections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].poId").value("PO2025001"))
                .andExpect(jsonPath("$[0].clientName").value("ABC Trading"))
                .andExpect(jsonPath("$[0].status").value("미수금"));

        verify(collectionQueryService).findAll();
    }

    @Test
    @DisplayName("매출·수금 현황 단건 조회 API 호출 시 200 OK와 현황을 반환한다")
    void getCollectionApi_whenCollectionExists_thenReturnsOkAndCollection() throws Exception {
        // given
        Collection collection = new Collection(
                1L,
                "PO2025001",
                "PO-2026-001",
                1L,
                "ABC Trading",
                new java.math.BigDecimal("15000.00"),
                new java.math.BigDecimal("10000.00"),
                new java.math.BigDecimal("5000.00"),
                "USD",
                "미수금",
                java.time.LocalDate.of(2026, 5, 15),
                java.time.LocalDateTime.of(2026, 3, 5, 9, 0),
                java.time.LocalDateTime.of(2026, 5, 15, 14, 0)
        );
        when(collectionQueryService.findById(1L)).thenReturn(collection);

        // when & then
        mockMvc.perform(get("/api/collections/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poId").value("PO2025001"))
                .andExpect(jsonPath("$.clientName").value("ABC Trading"))
                .andExpect(jsonPath("$.status").value("미수금"));

        verify(collectionQueryService).findById(1L);
    }

    @Test
    @DisplayName("출하현황 상태 변경 API 호출 시 200 OK와 변경된 출하현황을 반환한다")
    void updateShipmentStatusApi_whenRequestIsValid_thenReturnsOkAndUpdatedShipment() throws Exception {
        // given
        com.team2.documents.command.domain.entity.Shipment shipment =
                new com.team2.documents.command.domain.entity.Shipment(1L, "PO2025-0001", ShipmentStatus.COMPLETED);
        when(shipmentCommandService.updateStatus(1L, ShipmentStatus.COMPLETED)).thenReturn(shipment);

        // when & then
        mockMvc.perform(put("/api/shipments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ShipmentStatusUpdateRequest(ShipmentStatus.COMPLETED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentId").value(1))
                .andExpect(jsonPath("$.poId").value("PO2025-0001"))
                .andExpect(jsonPath("$.shipmentStatus").value("COMPLETED"));

        verify(shipmentCommandService).updateStatus(1L, ShipmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("수금 처리 API 호출 시 200 OK와 변경된 현황을 반환한다")
    void updateCollectionApi_whenRequestIsValid_thenReturnsOkAndUpdatedCollection() throws Exception {
        // given
        Collection collection = new Collection(
                1L,
                "PO2025001",
                "PO-2026-001",
                1L,
                "ABC Trading",
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                "USD",
                "수금완료",
                java.time.LocalDate.of(2026, 6, 1),
                java.time.LocalDateTime.of(2026, 3, 5, 9, 0),
                java.time.LocalDateTime.of(2026, 6, 1, 14, 0)
        );
        when(collectionCommandService.complete(1L, "수금완료", java.time.LocalDate.of(2026, 6, 1)))
                .thenReturn(collection);

        // when & then
        mockMvc.perform(put("/api/collections/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CollectionUpdateRequest(
                                        "수금완료",
                                        java.time.LocalDate.of(2026, 6, 1),
                                        "2차 잔금 입금"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poId").value("PO2025001"))
                .andExpect(jsonPath("$.status").value("수금완료"))
                .andExpect(jsonPath("$.collectionDate").value("2026-06-01"));

        verify(collectionCommandService).complete(1L, "수금완료", java.time.LocalDate.of(2026, 6, 1));
    }

    @Test
    @DisplayName("결재 요청 생성 API 호출 시 200 OK와 결재 요청을 반환한다")
    void createApprovalRequestApi_whenRequestIsValid_thenReturnsOkAndApprovalRequest() throws Exception {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L,
                ApprovalDocumentType.PO,
                "PO2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청드립니다.",
                null
        );
        when(approvalRequestCommandService.create(org.mockito.ArgumentMatchers.any(ApprovalRequestCreateRequest.class)))
                .thenReturn(approvalRequest);

        // when & then
        mockMvc.perform(post("/api/approval-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ApprovalRequestCreateRequest(
                                        ApprovalDocumentType.PO,
                                        "PO2025-0001",
                                        ApprovalRequestType.REGISTRATION,
                                        2L,
                                        1L,
                                        "결재 요청드립니다."))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvalRequestId").value(1))
                .andExpect(jsonPath("$.documentType").value("PO"))
                .andExpect(jsonPath("$.documentId").value("PO2025-0001"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(approvalRequestCommandService).create(org.mockito.ArgumentMatchers.any(ApprovalRequestCreateRequest.class));
    }

    @Test
    @DisplayName("결재 요청 처리 API 호출 시 200 OK와 변경된 결재 요청을 반환한다")
    void updateApprovalRequestApi_whenRequestIsValid_thenReturnsOkAndUpdatedApprovalRequest() throws Exception {
        // given
        ApprovalRequest approvalRequest = new ApprovalRequest(
                1L,
                ApprovalDocumentType.PO,
                "PO2025-0001",
                ApprovalRequestType.REGISTRATION,
                2L,
                1L,
                "결재 요청드립니다.",
                null
        );
        approvalRequest.setStatus(ApprovalStatus.APPROVED);
        when(approvalRequestCommandService.update(1L, ApprovalStatus.APPROVED, "확인 완료")).thenReturn(approvalRequest);

        // when & then
        mockMvc.perform(put("/api/approval-requests/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ApprovalRequestUpdateRequest(ApprovalStatus.APPROVED, "확인 완료"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvalRequestId").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(approvalRequestCommandService).update(1L, ApprovalStatus.APPROVED, "확인 완료");
    }

    @Test
    @DisplayName("PO 수정 가능 여부 검증 API 호출 시 200 OK를 반환한다")
    void validateModifiableApi_whenRequestIsValid_thenReturnsOk() throws Exception {
        // given
        String poId = "PO2025-0001";
        doNothing().when(purchaseOrderModificationService).validateModifiable(poId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders/{poId}/validate-modifiable", poId))
                .andExpect(status().isOk());

        verify(purchaseOrderModificationService).validateModifiable(poId);
    }

    @Test
    @DisplayName("PO 삭제 가능 여부 검증 API 호출 시 200 OK를 반환한다")
    void validateDeletableApi_whenRequestIsValid_thenReturnsOk() throws Exception {
        // given
        String poId = "PO2025-0001";
        doNothing().when(purchaseOrderModificationService).validateDeletable(poId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders/{poId}/validate-deletable", poId))
                .andExpect(status().isOk());

        verify(purchaseOrderModificationService).validateDeletable(poId);
    }

    @Test
    @DisplayName("PO 자동 생성 문서 생성 API 호출 시 200 OK를 반환한다")
    void generateDocumentsApi_whenRequestIsValid_thenReturnsOk() throws Exception {
        // given
        String poId = "PO2025-0001";
        doNothing().when(purchaseOrderDocumentGenerationService).generateOnConfirmation(poId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders/{poId}/generate-documents", poId))
                .andExpect(status().isOk());

        verify(purchaseOrderDocumentGenerationService).generateOnConfirmation(poId);
    }

    @Test
    @DisplayName("생산지시서 선택 생성 API 호출 시 200 OK를 반환한다")
    void generateProductionOrderApi_whenRequestIsValid_thenReturnsOk() throws Exception {
        // given
        String poId = "PO2025-0001";
        doNothing().when(purchaseOrderProductionOrderGenerationService).generate(poId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders/{poId}/generate-production-order", poId))
                .andExpect(status().isOk());

        verify(purchaseOrderProductionOrderGenerationService).generate(poId);
    }

    @Test
    @DisplayName("PO 승인 API 호출 시 200 OK를 반환한다")
    void approvePurchaseOrderApi_whenRequestIsValid_thenReturnsOk() throws Exception {
        // given
        String poId = "PO2025-0001";
        doNothing().when(purchaseOrderApprovalWorkflowService).approve(poId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders/{poId}/approve", poId))
                .andExpect(status().isOk());

        verify(purchaseOrderApprovalWorkflowService).approve(poId);
    }

    @Test
    @DisplayName("PO 반려 API 호출 시 200 OK를 반환한다")
    void rejectPurchaseOrderApi_whenRequestIsValid_thenReturnsOk() throws Exception {
        // given
        String poId = "PO2025-0001";
        doNothing().when(purchaseOrderRejectionWorkflowService).reject(poId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders/{poId}/reject", poId))
                .andExpect(status().isOk());

        verify(purchaseOrderRejectionWorkflowService).reject(poId);
    }

    @Test
    @DisplayName("PI 승인 API 호출 시 200 OK를 반환한다")
    void approveProformaInvoiceApi_whenRequestIsValid_thenReturnsOk() throws Exception {
        // given
        String piId = "PI2025-0001";
        doNothing().when(proformaInvoiceApprovalWorkflowService).approve(piId);

        // when & then
        mockMvc.perform(post("/api/proforma-invoices/{piId}/approve", piId))
                .andExpect(status().isOk());

        verify(proformaInvoiceApprovalWorkflowService).approve(piId);
    }

    @Test
    @DisplayName("PI 반려 API 호출 시 200 OK를 반환한다")
    void rejectProformaInvoiceApi_whenRequestIsValid_thenReturnsOk() throws Exception {
        // given
        String piId = "PI2025-0001";
        doNothing().when(proformaInvoiceRejectionWorkflowService).reject(piId);

        // when & then
        mockMvc.perform(post("/api/proforma-invoices/{piId}/reject", piId))
                .andExpect(status().isOk());

        verify(proformaInvoiceRejectionWorkflowService).reject(piId);
    }
}
