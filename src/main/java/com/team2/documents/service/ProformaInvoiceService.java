package com.team2.documents.service;

import org.springframework.stereotype.Service;

import com.team2.documents.entity.PositionLevel;
import com.team2.documents.entity.ProformaInvoice;
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

        if (PositionLevel.MANAGER.equals(positionLevel)) {
            proformaInvoice.confirmRegistration();
            return;
        }

        proformaInvoice.requestRegistration();
        if (PositionLevel.STAFF.equals(positionLevel)) {
            approvalRequestRepository.createForProformaInvoice(piId, userId);
        }
    }
}
