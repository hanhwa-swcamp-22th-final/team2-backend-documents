package com.team2.documents.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team2.documents.common.error.ResourceNotFoundException;
import com.team2.documents.query.mapper.ApprovalRequestQueryMapper;
import com.team2.documents.query.model.ApprovalRequestView;

@ExtendWith(MockitoExtension.class)
class ApprovalRequestQueryServiceTest {

    @Mock
    private ApprovalRequestQueryMapper approvalRequestQueryMapper;

    @InjectMocks
    private ApprovalRequestQueryService approvalRequestQueryService;

    @Test
    @DisplayName("결재 요청 ID로 조회 시 해당 결재 요청을 반환한다")
    void findById_whenApprovalRequestExists_thenReturnsApprovalRequest() {
        // given
        ApprovalRequestView approvalRequest = new ApprovalRequestView();
        approvalRequest.setApprovalRequestId(1L);
        approvalRequest.setDocumentType("PO");
        approvalRequest.setDocumentId("PO2025-0001");
        approvalRequest.setRequestType("REGISTRATION");
        approvalRequest.setRequesterId(2L);
        approvalRequest.setApproverId(1L);
        approvalRequest.setComment("결재 요청");
        when(approvalRequestQueryMapper.findById(1L)).thenReturn(approvalRequest);

        // when
        ApprovalRequestView result = approvalRequestQueryService.findById(1L);

        // then
        assertEquals(1L, result.getApprovalRequestId());
    }

    @Test
    @DisplayName("존재하지 않는 결재 요청 ID로 조회 시 예외를 던진다")
    void findById_whenApprovalRequestNotExists_thenThrowsException() {
        // given
        when(approvalRequestQueryMapper.findById(999L)).thenReturn(null);

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> approvalRequestQueryService.findById(999L));
    }

    @Test
    @DisplayName("문서 유형, 문서 ID, 상태로 결재 요청을 조회한다")
    void findByDocumentTypeAndDocumentIdAndStatus_whenExists_thenReturnsApprovalRequest() {
        // given
        ApprovalRequestView approvalRequest = new ApprovalRequestView();
        approvalRequest.setApprovalRequestId(1L);
        approvalRequest.setDocumentType("PO");
        approvalRequest.setDocumentId("PO2025-0001");
        approvalRequest.setRequestType("REGISTRATION");
        approvalRequest.setRequesterId(2L);
        approvalRequest.setApproverId(1L);
        approvalRequest.setComment("결재 요청");
        when(approvalRequestQueryMapper.findByDocumentTypeAndDocumentIdAndStatus("PO", "PO2025-0001", "PENDING"))
                .thenReturn(approvalRequest);

        // when
        ApprovalRequestView result = approvalRequestQueryService.findByDocumentTypeAndDocumentIdAndStatus(
                "PO", "PO2025-0001", "PENDING");

        // then
        assertEquals("PO2025-0001", result.getDocumentId());
    }

    @Test
    @DisplayName("조건에 맞는 결재 요청이 없으면 예외를 던진다")
    void findByDocumentTypeAndDocumentIdAndStatus_whenNotExists_thenThrowsException() {
        // given
        when(approvalRequestQueryMapper.findByDocumentTypeAndDocumentIdAndStatus("PO", "NOT-EXIST", "PENDING"))
                .thenReturn(null);

        // when & then
        assertThrows(ResourceNotFoundException.class,
                () -> approvalRequestQueryService.findByDocumentTypeAndDocumentIdAndStatus(
                        "PO", "NOT-EXIST", "PENDING"));
    }

    @Test
    @DisplayName("전체 결재 요청 목록을 조회한다")
    void findAll_whenApprovalRequestsExist_thenReturnsAll() {
        // given
        ApprovalRequestView approvalRequest = new ApprovalRequestView();
        approvalRequest.setApprovalRequestId(1L);
        approvalRequest.setDocumentType("PO");
        approvalRequest.setDocumentId("PO2025-0001");
        approvalRequest.setRequestType("REGISTRATION");
        approvalRequest.setRequesterId(2L);
        approvalRequest.setApproverId(1L);
        approvalRequest.setComment("결재 요청");
        when(approvalRequestQueryMapper.findAll()).thenReturn(List.of(approvalRequest));

        // when
        List<ApprovalRequestView> result = approvalRequestQueryService.findAll();

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
