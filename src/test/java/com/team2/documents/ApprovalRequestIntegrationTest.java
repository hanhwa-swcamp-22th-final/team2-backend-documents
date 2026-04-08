package com.team2.documents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.ApprovalRequestCreateRequest;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import com.team2.documents.command.domain.entity.enums.ApprovalStatus;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.repository.ApprovalRequestRepository;
import com.team2.documents.command.domain.repository.ProformaInvoiceRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@WithMockUser(username = "test-admin", roles = {"ADMIN"})
class ApprovalRequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM approval_requests");
        jdbcTemplate.execute("DELETE FROM proforma_invoices");
    }

    @Test
    @DisplayName("결재 요청 생성 API는 H2에 approval request를 저장한다")
    void createApprovalRequest_persistsApprovalRequestInH2() throws Exception {
        proformaInvoiceRepository.save(new ProformaInvoice("PI260001", ProformaInvoiceStatus.DRAFT));

        mockMvc.perform(post("/api/approval-requests")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ApprovalRequestCreateRequest(
                                        ApprovalDocumentType.PI,
                                        "PI260001",
                                        ApprovalRequestType.REGISTRATION,
                                        2L,
                                        1L,
                                        "결재 요청"
                                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentType").value("PI"))
                .andExpect(jsonPath("$.documentId").value("PI260001"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        assertThat(approvalRequestRepository.findAll())
                .singleElement()
                .satisfies(approvalRequest -> {
                    assertThat(approvalRequest.getDocumentType()).isEqualTo(ApprovalDocumentType.PI);
                    assertThat(approvalRequest.getDocumentId()).isEqualTo("PI260001");
                    assertThat(approvalRequest.getStatus()).isEqualTo(ApprovalStatus.PENDING);
                });
    }
}
