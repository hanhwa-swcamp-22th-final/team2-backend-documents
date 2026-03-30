package com.team2.documents.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.ProformaInvoice;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalStatus;
import com.team2.documents.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.ProformaInvoiceRepository;

@Service
public class ProformaInvoiceRejectionWorkflowService {

    private final ProformaInvoiceRepository proformaInvoiceRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    public ProformaInvoiceRejectionWorkflowService(ProformaInvoiceRepository proformaInvoiceRepository,
                                                   ApprovalRequestRepository approvalRequestRepository) {
        this.proformaInvoiceRepository = proformaInvoiceRepository;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    public void reject(String piId) {
        ProformaInvoice proformaInvoice = proformaInvoiceRepository.findById(piId)
                .orElseThrow(() -> new IllegalArgumentException("PI 정보를 찾을 수 없습니다."));
        if (!ProformaInvoiceStatus.APPROVAL_PENDING.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("결재대기 상태의 PI만 반려할 수 있습니다.");
        }
        proformaInvoice.setStatus(ProformaInvoiceStatus.REJECTED);

        ApprovalRequest approvalRequest = approvalRequestRepository.findPendingByDocument(ApprovalDocumentType.PI, piId)
                .orElseThrow(() -> new IllegalArgumentException("대기 중인 결재 요청을 찾을 수 없습니다."));
        approvalRequest.setStatus(ApprovalStatus.REJECTED);
        approvalRequest.setReviewedAt(LocalDateTime.now());
    }
}
