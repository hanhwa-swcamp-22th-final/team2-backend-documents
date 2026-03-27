package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ApprovalDocumentType;
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
        proformaInvoiceRepository.findById(piId)
                .orElseThrow(() -> new IllegalArgumentException("PI 정보를 찾을 수 없습니다."))
                .reject();

        approvalRequestRepository.findPendingByDocument(ApprovalDocumentType.PI, piId)
                .orElseThrow(() -> new IllegalArgumentException("대기 중인 결재 요청을 찾을 수 없습니다."))
                .reject();
    }
}
