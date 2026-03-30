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
import com.team2.documents.entity.ProformaInvoice;
import com.team2.documents.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.ProformaInvoiceRepository;
import com.team2.documents.repository.UserPositionRepository;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceServiceTest {

    @Mock
    private ProformaInvoiceRepository proformaInvoiceRepository;

    @Mock
    private UserPositionRepository userPositionRepository;

    @Mock
    private ApprovalRequestRepository approvalRequestRepository;

    @InjectMocks
    private ProformaInvoiceService proformaInvoiceService;

    @Test
    @DisplayName("일반 직원이 PI 등록 요청을 하면 상태를 결재대기로 바꾸고 결재 요청을 생성한다")
    void requestRegistration_whenStaffRequests_thenChangesStatusAndCreatesApprovalRequest() {
        // given
        String piId = "PI2025-0001";
        Long userId = 2L;
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.DRAFT);

        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.of(proformaInvoice));
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.STAFF));

        // when
        proformaInvoiceService.requestRegistration(piId, userId);

        // then
        assertEquals(ProformaInvoiceStatus.APPROVAL_PENDING, proformaInvoice.getStatus());
        verify(approvalRequestRepository).save(any(com.team2.documents.entity.ApprovalRequest.class));
    }

    @Test
    @DisplayName("팀장이 PI 등록 요청을 하면 즉시 확정되고 결재 요청이 생성되지 않는다")
    void requestRegistration_whenManagerRequests_thenConfirmsImmediatelyAndDoesNotCreateApprovalRequest() {
        // given
        String piId = "PI2025-0001";
        Long userId = 1L;
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.DRAFT);

        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.of(proformaInvoice));
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));

        // when
        proformaInvoiceService.requestRegistration(piId, userId);

        // then
        assertEquals(ProformaInvoiceStatus.CONFIRMED, proformaInvoice.getStatus());
        org.mockito.Mockito.verify(approvalRequestRepository, org.mockito.Mockito.never())
                .save(any(com.team2.documents.entity.ApprovalRequest.class));
    }

    @Test
    @DisplayName("PI 정보가 없으면 등록 요청 시 예외가 발생한다")
    void requestRegistration_whenProformaInvoiceDoesNotExist_thenThrowsException() {
        // given
        String piId = "PI2025-9999";
        Long userId = 2L;
        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> proformaInvoiceService.requestRegistration(piId, userId));
    }

    @Test
    @DisplayName("사용자 직급 정보가 없으면 등록 요청 시 예외가 발생한다")
    void requestRegistration_whenPositionLevelDoesNotExist_thenThrowsException() {
        // given
        String piId = "PI2025-0001";
        Long userId = 99L;
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.DRAFT);

        when(proformaInvoiceRepository.findById(piId)).thenReturn(Optional.of(proformaInvoice));
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> proformaInvoiceService.requestRegistration(piId, userId));
    }
}
