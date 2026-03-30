package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.ProformaInvoice;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.repository.ApprovalRequestRepository;
import com.team2.documents.repository.ProformaInvoiceRepository;
import com.team2.documents.repository.UserPositionRepository;

@Service
public class ProformaInvoiceService {

    private final ProformaInvoiceRepository proformaInvoiceRepository;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    public ProformaInvoiceService(ProformaInvoiceRepository proformaInvoiceRepository,
                                  UserPositionRepository userPositionRepository,
                                  ApprovalRequestRepository approvalRequestRepository) {
        this.proformaInvoiceRepository = proformaInvoiceRepository;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    public void requestRegistration(String piId, Long userId) {
        ProformaInvoice proformaInvoice = proformaInvoiceRepository.findById(piId)
                .orElseThrow(() -> new IllegalArgumentException("PI 정보를 찾을 수 없습니다."));

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!com.team2.documents.entity.enums.ProformaInvoiceStatus.DRAFT.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("초안 상태의 PI만 등록 요청할 수 있습니다.");
        }

        if (PositionLevel.MANAGER.equals(positionLevel)) {
            proformaInvoice.setStatus(com.team2.documents.entity.enums.ProformaInvoiceStatus.CONFIRMED);
            return;
        }

        proformaInvoice.setStatus(com.team2.documents.entity.enums.ProformaInvoiceStatus.APPROVAL_PENDING);
        if (PositionLevel.STAFF.equals(positionLevel)) {
            approvalRequestRepository.save(new ApprovalRequest(
                    ApprovalDocumentType.PI,
                    piId,
                    ApprovalRequestType.REGISTRATION,
                    userId,
                    1L
            ));
        }
    }
}
