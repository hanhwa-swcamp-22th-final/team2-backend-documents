package com.team2.documents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.PurchaseOrderCreateRequest;
import com.team2.documents.command.application.dto.PurchaseOrderItemCreateRequest;
import com.team2.documents.command.application.dto.PurchaseOrderRegistrationRequest;
import com.team2.documents.command.domain.entity.CommercialInvoice;
import com.team2.documents.command.domain.entity.DocsRevision;
import com.team2.documents.command.domain.entity.PackingList;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.domain.repository.CommercialInvoiceJpaRepository;
import com.team2.documents.command.domain.repository.DocsRevisionRepository;
import com.team2.documents.command.domain.repository.PackingListJpaRepository;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@org.springframework.security.test.context.support.WithMockUser(username = "test-admin", roles = {"ADMIN"})
class DocumentWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private CommercialInvoiceJpaRepository commercialInvoiceJpaRepository;

    @Autowired
    private PackingListJpaRepository packingListJpaRepository;

    @Autowired
    private DocsRevisionRepository docsRevisionRepository;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM docs_revision");
        jdbcTemplate.execute("DELETE FROM shipment_orders");
        jdbcTemplate.execute("DELETE FROM packing_lists");
        jdbcTemplate.execute("DELETE FROM commercial_invoices");
        jdbcTemplate.execute("DELETE FROM approval_requests");
        jdbcTemplate.execute("DELETE FROM po_items");
        jdbcTemplate.execute("DELETE FROM purchase_orders");
        jdbcTemplate.execute("DELETE FROM document_number_sequences");
    }

    @Test
    @DisplayName("PO 생성부터 등록 요청, CI/PL/SO 생성, docs_revision 저장까지 수직 경로로 검증한다")
    void purchaseOrderWorkflow_persistsDocumentsAndSnapshotsThroughFullStack() throws Exception {
        when(userPositionRepository.findPositionLevelByUserId(1L)).thenReturn(java.util.Optional.of(PositionLevel.MANAGER));

        PurchaseOrderCreateRequest request = new PurchaseOrderCreateRequest(
                "PO260001",
                null,
                LocalDate.of(2026, 4, 2),
                10,
                1,
                1L,
                LocalDate.of(2026, 4, 20),
                "FOB",
                "Busan",
                LocalDate.of(2026, 4, 18),
                false,
                null,
                "ABC Trading",
                "Seoul",
                "KR",
                "USD",
                "Kim",
                1L,
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

        mockMvc.perform(post("/api/purchase-orders")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poId").value("PO260001"));

        mockMvc.perform(post("/api/purchase-orders/request-registration")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PurchaseOrderRegistrationRequest("PO260001", 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PO 등록 요청이 처리되었습니다."));

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPoCode("PO260001").orElseThrow();
        assertThat(purchaseOrder.getPurchaseOrderId()).isNotNull();
        assertThat(purchaseOrder.getStatus().name()).isEqualTo("CONFIRMED");

        CommercialInvoice commercialInvoice = commercialInvoiceJpaRepository.findByCiCode("CI260001").orElseThrow();
        PackingList packingList = packingListJpaRepository.findByPlCode("PL260001").orElseThrow();

        assertThat(commercialInvoice.getPoId()).isEqualTo(purchaseOrder.getPurchaseOrderId());
        assertThat(packingList.getPoId()).isEqualTo(purchaseOrder.getPurchaseOrderId());

        mockMvc.perform(get("/api/commercial-invoices/{ciId}", "CI260001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ciId").value("CI260001"));

        mockMvc.perform(get("/api/packing-lists/{plId}", "PL260001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plId").value("PL260001"));

        mockMvc.perform(get("/api/shipment-orders/{shipmentOrderId}", "SO260001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentOrderId").value("SO260001"));

        List<DocsRevision> revisions = docsRevisionRepository.findAll().stream()
                .sorted(Comparator.comparing(DocsRevision::getDocsRevisionId))
                .toList();

        assertThat(revisions).hasSize(6);
        assertThat(revisions.stream()
                .map(DocsRevision::getSnapshotData)
                .collect(Collectors.joining("\n")))
                .contains("\"entryType\":\"SNAPSHOT\"")
                .contains("\"entryType\":\"REVISION\"")
                .contains("\"poId\":\"PO260001\"")
                .contains("\"ciId\":\"CI260001\"")
                .contains("\"plId\":\"PL260001\"")
                .contains("\"action\":\"CREATE\"")
                .contains("\"action\":\"REQUEST_REGISTRATION\"")
                .contains("\"action\":\"GENERATE_FOLLOW_UP_DOCUMENTS\"");
        assertThat(revisions.stream()
                .filter(revision -> "PO".equals(revision.getDocType()))
                .map(DocsRevision::getDocId))
                .contains(purchaseOrder.getPurchaseOrderId());
        assertThat(revisions.stream()
                .filter(revision -> "CI".equals(revision.getDocType()))
                .map(DocsRevision::getDocId))
                .contains(commercialInvoice.getCommercialInvoiceId());
        assertThat(revisions.stream()
                .filter(revision -> "PL".equals(revision.getDocType()))
                .map(DocsRevision::getDocId))
                .contains(packingList.getPackingListId());
    }

    @Test
    @DisplayName("존재하지 않는 PO 등록 요청은 전역 예외 처리기로 400 응답을 반환한다")
    void requestRegistration_whenPurchaseOrderDoesNotExist_thenReturnsGlobalErrorResponse() throws Exception {
        when(userPositionRepository.findPositionLevelByUserId(1L)).thenReturn(java.util.Optional.of(PositionLevel.MANAGER));

        mockMvc.perform(post("/api/purchase-orders/request-registration")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PurchaseOrderRegistrationRequest("PO-NOT-FOUND", 1L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("PO 정보를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.path").value("/api/purchase-orders/request-registration"));
    }
}
