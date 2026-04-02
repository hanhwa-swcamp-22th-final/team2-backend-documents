package com.team2.documents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.ApprovalRequestCreateRequest;
import com.team2.documents.command.application.dto.CollectionUpdateRequest;
import com.team2.documents.command.application.dto.ShipmentStatusUpdateRequest;
import com.team2.documents.command.domain.entity.Collection;
import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import com.team2.documents.command.domain.repository.ApprovalRequestRepository;
import com.team2.documents.command.domain.repository.CollectionRepository;
import com.team2.documents.command.domain.repository.CommercialInvoiceRepository;
import com.team2.documents.command.domain.repository.PackingListRepository;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;
import com.team2.documents.command.domain.repository.ShipmentOrderRepository;
import com.team2.documents.command.domain.repository.ShipmentRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DocumentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @MockitoBean
    private CommercialInvoiceRepository commercialInvoiceRepository;

    @MockitoBean
    private PackingListRepository packingListRepository;

    @MockitoBean
    private ShipmentOrderRepository shipmentOrderRepository;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @Test
    @DisplayName("출하현황 상태 변경 API는 실제 DB 상태를 변경한다")
    void updateShipmentStatus_updatesShipmentInH2() throws Exception {
        Shipment shipment = shipmentRepository.save(new Shipment(1L, "PO2025-0001", ShipmentStatus.READY));

        mockMvc.perform(put("/api/shipments/{id}", shipment.getShipmentId())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ShipmentStatusUpdateRequest(ShipmentStatus.COMPLETED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentStatus").value("COMPLETED"));

        Shipment updatedShipment = shipmentRepository.findById(shipment.getShipmentId()).orElseThrow();
        assertThat(updatedShipment.getShipmentStatus()).isEqualTo(ShipmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("수금 처리 API는 실제 DB 상태와 수금일을 변경한다")
    void updateCollection_updatesCollectionInH2() throws Exception {
        Collection collection = collectionRepository.save(new Collection(
                null,
                "PO2025-0002",
                "PO-2025-0002",
                10L,
                "ABC Trading",
                new BigDecimal("100000"),
                null,
                null,
                "USD",
                "미수금",
                null,
                null,
                null
        ));

        mockMvc.perform(put("/api/collections/{collectionId}", collection.getCollectionId())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CollectionUpdateRequest("수금완료", LocalDate.of(2026, 3, 30), "completed"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("수금완료"))

                .andExpect(jsonPath("$.collectionDate").value("2026-03-30"));

        Collection updatedCollection = collectionRepository.findById(collection.getCollectionId()).orElseThrow();
        assertThat(updatedCollection.getStatus()).isEqualTo("수금완료");
        assertThat(updatedCollection.getCollectionDate()).isEqualTo(LocalDate.of(2026, 3, 30));
    }

    @Test
    @DisplayName("생산지시서 목록 조회 API는 H2에 저장된 데이터를 반환한다")
    void getProductionOrders_returnsPersistedProductionOrders() throws Exception {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(new PurchaseOrder("PO2025-0003", PurchaseOrderStatus.DRAFT));
        productionOrderRepository.save(new ProductionOrder(
                "PRD-2026-001",
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                LocalDate.of(2026, 3, 30),
                LocalDate.of(2026, 4, 5),
                "진행중",
                List.of("item-1"),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        mockMvc.perform(get("/api/production-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productionOrderId").value("PRD-2026-001"))
                .andExpect(jsonPath("$[0].poId").value("PO2025-0003"))
                .andExpect(jsonPath("$[0].status").value("진행중"));
    }

    @Test
    @DisplayName("결재 요청 생성 API는 H2에 approval request를 저장한다")
    void createApprovalRequest_persistsApprovalRequestInH2() throws Exception {
        proformaInvoiceRepository.save(new ProformaInvoice("PI2025-0001", ProformaInvoiceStatus.DRAFT));

        mockMvc.perform(post("/api/approval-requests")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ApprovalRequestCreateRequest(
                                        ApprovalDocumentType.PI,
                                        "PI2025-0001",
                                        ApprovalRequestType.REGISTRATION,
                                        2L,
                                        1L,
                                        "결재 요청"
                                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentType").value("PI"))
                .andExpect(jsonPath("$.documentId").value("PI2025-0001"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        assertThat(approvalRequestRepository.findAll())
                .singleElement()
                .satisfies(approvalRequest -> {
                    assertThat(approvalRequest.getDocumentType()).isEqualTo(ApprovalDocumentType.PI);
                    assertThat(approvalRequest.getDocumentId()).isEqualTo("PI2025-0001");
                    assertThat(approvalRequest.getStatus()).isEqualTo(ApprovalStatus.PENDING);
                });
    }
}
