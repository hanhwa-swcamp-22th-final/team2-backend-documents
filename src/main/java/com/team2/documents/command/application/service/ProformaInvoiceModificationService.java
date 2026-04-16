package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;

@Service
@Transactional
public class ProformaInvoiceModificationService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public ProformaInvoiceModificationService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public void validateDeletable(String piId) {
        long poCount = purchaseOrderRepository.countByPiIdAndStatusNot(piId, PurchaseOrderStatus.DELETED);
        if (poCount > 0) {
            throw new IllegalStateException(
                    "참조 PO " + poCount + "건이 존재하여 삭제할 수 없습니다.");
        }
    }

    /**
     * Returns null if deletable, or a descriptive reason string if not.
     * Used by the approval workflow for defensive re-validation.
     */
    public String checkDeletable(String piId) {
        long poCount = purchaseOrderRepository.countByPiIdAndStatusNot(piId, PurchaseOrderStatus.DELETED);
        if (poCount > 0) {
            return "참조 PO " + poCount + "건이 존재하여 삭제할 수 없습니다.";
        }
        return null;
    }
}
