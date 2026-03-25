package com.team2.documents.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.dto.ProformaInvoiceRegistrationRequest;
import com.team2.documents.entity.PurchaseOrderStatus;
import com.team2.documents.service.ProformaInvoiceService;
import com.team2.documents.service.PurchaseOrderCreationService;
import com.team2.documents.service.PurchaseOrderModificationService;

@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseOrderModificationService purchaseOrderModificationService;

    @MockBean
    private PurchaseOrderCreationService purchaseOrderCreationService;

    @MockBean
    private ProformaInvoiceService proformaInvoiceService;

    @Test
    @DisplayName("PO 생성 API 호출 시 200 OK를 반환하고 Service를 호출한다")
    void createApi_whenRequestIsValid_thenReturnsOkAndCallsService() throws Exception {
        // given
        Long userId = 2L;
        doNothing().when(purchaseOrderCreationService).create(userId);

        // when & then
        mockMvc.perform(post("/api/purchase-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new com.team2.documents.dto.PurchaseOrderCreateRequest(userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("PO 생성 요청이 처리되었습니다."));

        verify(purchaseOrderCreationService).create(userId);
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
    @DisplayName("PO 초기 상태 조회 API 호출 시 200 OK와 상태값을 반환한다")
    void determineInitialStatusApi_whenRequestIsValid_thenReturnsOkAndStatus() throws Exception {
        // given
        Long userId = 1L;
        when(purchaseOrderCreationService.determineInitialStatus(userId))
                .thenReturn(PurchaseOrderStatus.CONFIRMED);

        // when & then
        mockMvc.perform(get("/api/purchase-orders/initial-status/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(purchaseOrderCreationService).determineInitialStatus(userId);
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
}
