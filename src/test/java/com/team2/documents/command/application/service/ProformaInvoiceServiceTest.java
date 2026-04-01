package com.team2.documents.command.application.service;

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

import com.team2.documents.command.domain.entity.enums.PositionLevel;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.domain.repository.UserPositionRepository;
import com.team2.documents.command.application.service.ApprovalRequestCommandService;
import com.team2.documents.command.application.service.ProformaInvoiceCommandService;

@ExtendWith(MockitoExtension.class)
class ProformaInvoiceServiceTest {

    @Mock
    private ProformaInvoiceCommandService proformaInvoiceCommandService;

    @Mock
    private UserPositionRepository userPositionRepository;

    @Mock
    private ApprovalRequestCommandService approvalRequestCommandService;

    @InjectMocks
    private ProformaInvoiceService proformaInvoiceService;

    @Test
    @DisplayName("일반 직원이 PI 등록 요청을 하면 상태를 결재대기로 바꾸고 결재 요청을 생성한다")
    void requestRegistration_whenStaffRequests_thenChangesStatusAndCreatesApprovalRequest() {
        // given
        String piId = "PI2025-0001";
        Long userId = 2L;
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.DRAFT);

        when(proformaInvoiceCommandService.findById(piId)).thenReturn(proformaInvoice);
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.STAFF));

        // when
        proformaInvoiceService.requestRegistration(piId, userId);

        // then
        assertEquals(ProformaInvoiceStatus.APPROVAL_PENDING, proformaInvoice.getStatus());
        verify(approvalRequestCommandService).save(any(com.team2.documents.command.domain.entity.ApprovalRequest.class));
    }

    @Test
    @DisplayName("팀장이 PI 등록 요청을 하면 즉시 확정되고 결재 요청이 생성되지 않는다")
    void requestRegistration_whenManagerRequests_thenConfirmsImmediatelyAndDoesNotCreateApprovalRequest() {
        // given
        String piId = "PI2025-0001";
        Long userId = 1L;
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.DRAFT);

        when(proformaInvoiceCommandService.findById(piId)).thenReturn(proformaInvoice);
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.MANAGER));

        // when
        proformaInvoiceService.requestRegistration(piId, userId);

        // then
        assertEquals(ProformaInvoiceStatus.CONFIRMED, proformaInvoice.getStatus());
        org.mockito.Mockito.verify(approvalRequestCommandService, org.mockito.Mockito.never())
                .save(any(com.team2.documents.command.domain.entity.ApprovalRequest.class));
    }

    @Test
    @DisplayName("PI 정보가 없으면 등록 요청 시 예외가 발생한다")
    void requestRegistration_whenProformaInvoiceDoesNotExist_thenThrowsException() {
        // given
        String piId = "PI2025-9999";
        Long userId = 2L;
        when(proformaInvoiceCommandService.findById(piId))
                .thenThrow(new IllegalArgumentException("PI 정보를 찾을 수 없습니다."));

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

        when(proformaInvoiceCommandService.findById(piId)).thenReturn(proformaInvoice);
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.empty());

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> proformaInvoiceService.requestRegistration(piId, userId));
    }

    @Test
    @DisplayName("초안 상태가 아닌 PI는 등록 요청할 수 없다")
    void requestRegistration_whenProformaInvoiceIsNotDraft_thenThrowsException() {
        // given
        String piId = "PI2025-0002";
        Long userId = 2L;
        ProformaInvoice proformaInvoice = new ProformaInvoice(piId, ProformaInvoiceStatus.CONFIRMED);

        when(proformaInvoiceCommandService.findById(piId)).thenReturn(proformaInvoice);
        when(userPositionRepository.findPositionLevelByUserId(userId))
                .thenReturn(Optional.of(PositionLevel.STAFF));

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> proformaInvoiceService.requestRegistration(piId, userId));
    }
}
