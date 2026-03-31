package com.team2.documents.command.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.entity.ApprovalRequest;
import com.team2.documents.entity.enums.PositionLevel;
import com.team2.documents.entity.ProformaInvoice;
import com.team2.documents.entity.enums.ApprovalDocumentType;
import com.team2.documents.entity.enums.ApprovalRequestType;
import com.team2.documents.entity.enums.ProformaInvoiceStatus;
import com.team2.documents.command.repository.UserPositionRepository;

@Service
@Transactional
public class ProformaInvoiceService {

    private final ProformaInvoiceCommandService proformaInvoiceCommandService;
    private final UserPositionRepository userPositionRepository;
    private final ApprovalRequestCommandService approvalRequestCommandService;

    public ProformaInvoiceService(ProformaInvoiceCommandService proformaInvoiceCommandService,
                                  UserPositionRepository userPositionRepository,
                                  ApprovalRequestCommandService approvalRequestCommandService) {
        this.proformaInvoiceCommandService = proformaInvoiceCommandService;
        this.userPositionRepository = userPositionRepository;
        this.approvalRequestCommandService = approvalRequestCommandService;
    }

    public void requestRegistration(String piId, Long userId) {
        ProformaInvoice proformaInvoice = proformaInvoiceCommandService.findById(piId);

        PositionLevel positionLevel = userPositionRepository.findPositionLevelByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 직급 정보를 찾을 수 없습니다."));

        if (!ProformaInvoiceStatus.DRAFT.equals(proformaInvoice.getStatus())) {
            throw new IllegalStateException("초안 상태의 PI만 등록 요청할 수 있습니다.");
        }

        if (PositionLevel.MANAGER.equals(positionLevel)) {
            proformaInvoice.setStatus(ProformaInvoiceStatus.CONFIRMED);
            proformaInvoiceCommandService.save(proformaInvoice);
            return;
        }

        proformaInvoice.setStatus(ProformaInvoiceStatus.APPROVAL_PENDING);
        proformaInvoiceCommandService.save(proformaInvoice);
        if (PositionLevel.STAFF.equals(positionLevel)) {
            approvalRequestCommandService.save(new ApprovalRequest(
                    ApprovalDocumentType.PI,
                    piId,
                    ApprovalRequestType.REGISTRATION,
                    userId,
                    1L
            ));
        }
    }
}
