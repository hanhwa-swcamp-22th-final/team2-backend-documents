package com.team2.documents.command.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.repository.CollectionRepository;
import com.team2.documents.command.domain.repository.CommercialInvoiceJpaRepository;
import com.team2.documents.command.domain.repository.PackingListJpaRepository;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.ShipmentRepository;

@Service
@Transactional
public class PurchaseOrderModificationService {

    private static final String COLLECTION_COMPLETED = "수금완료";

    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final CommercialInvoiceJpaRepository commercialInvoiceJpaRepository;
    private final PackingListJpaRepository packingListJpaRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final ShipmentRepository shipmentRepository;
    private final CollectionRepository collectionRepository;

    public PurchaseOrderModificationService(PurchaseOrderCommandService purchaseOrderCommandService,
                                            CommercialInvoiceJpaRepository commercialInvoiceJpaRepository,
                                            PackingListJpaRepository packingListJpaRepository,
                                            ProductionOrderRepository productionOrderRepository,
                                            ShipmentRepository shipmentRepository,
                                            CollectionRepository collectionRepository) {
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.commercialInvoiceJpaRepository = commercialInvoiceJpaRepository;
        this.packingListJpaRepository = packingListJpaRepository;
        this.productionOrderRepository = productionOrderRepository;
        this.shipmentRepository = shipmentRepository;
        this.collectionRepository = collectionRepository;
    }

    /**
     * 출하완료된 출하 또는 수금완료된 수금이 있는 PO 는 수정 불가.
     * 진행중(preparing/미수금) 후속문서는 PO 수정 반영을 허용.
     */
    public void validateModifiable(String poId) {
        String reason = checkModifiable(poId);
        if (reason != null) {
            throw new IllegalStateException(reason);
        }
    }

    /**
     * @return null if modifiable, or a descriptive reason string if not
     */
    public String checkModifiable(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        Long poLongId = purchaseOrder.getPurchaseOrderId();

        List<String> blockers = new ArrayList<>();

        long completedShipmentCount = shipmentRepository.countCompletedByPoCode(poId);
        if (completedShipmentCount > 0) {
            blockers.add("출하완료 " + completedShipmentCount + "건");
        }

        long completedCollectionCount =
                collectionRepository.countByPoIdAndStatus(poLongId, COLLECTION_COMPLETED);
        if (completedCollectionCount > 0) {
            blockers.add("수금완료 " + completedCollectionCount + "건");
        }

        if (!blockers.isEmpty()) {
            return String.join(", ", blockers) + "이(가) 존재하여 수정할 수 없습니다.";
        }
        return null;
    }

    public void validateDeletable(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        Long poLongId = purchaseOrder.getPurchaseOrderId();

        List<String> blockers = new ArrayList<>();

        long shipmentCount = shipmentRepository.countByPoCode(poId);
        if (shipmentCount > 0) {
            blockers.add("출하(Shipment) " + shipmentCount + "건");
        }

        long ciCount = commercialInvoiceJpaRepository.countByPoId(poLongId);
        if (ciCount > 0) {
            blockers.add("상업송장(CI) " + ciCount + "건");
        }

        long plCount = packingListJpaRepository.countByPoId(poLongId);
        if (plCount > 0) {
            blockers.add("패킹리스트(PL) " + plCount + "건");
        }

        long productionCount = productionOrderRepository.countByPoId(poLongId);
        if (productionCount > 0) {
            blockers.add("생산지시서 " + productionCount + "건");
        }

        long collectionCount = collectionRepository.countByPoId(poLongId);
        if (collectionCount > 0) {
            blockers.add("수금 " + collectionCount + "건");
        }

        if (!blockers.isEmpty()) {
            throw new IllegalStateException(
                    String.join(", ", blockers) + "이(가) 존재하여 삭제할 수 없습니다.");
        }
    }

    /**
     * Validates that a PO has no downstream documents and returns a descriptive
     * message when it does. Used by the approval workflow for defensive re-validation.
     *
     * @return null if deletable, or a descriptive reason string if not
     */
    public String checkDeletable(String poId) {
        PurchaseOrder purchaseOrder = purchaseOrderCommandService.findById(poId);
        Long poLongId = purchaseOrder.getPurchaseOrderId();

        List<String> blockers = new ArrayList<>();

        long shipmentCount = shipmentRepository.countByPoCode(poId);
        if (shipmentCount > 0) {
            blockers.add("출하(Shipment) " + shipmentCount + "건");
        }

        long ciCount = commercialInvoiceJpaRepository.countByPoId(poLongId);
        if (ciCount > 0) {
            blockers.add("상업송장(CI) " + ciCount + "건");
        }

        long plCount = packingListJpaRepository.countByPoId(poLongId);
        if (plCount > 0) {
            blockers.add("패킹리스트(PL) " + plCount + "건");
        }

        long productionCount = productionOrderRepository.countByPoId(poLongId);
        if (productionCount > 0) {
            blockers.add("생산지시서 " + productionCount + "건");
        }

        long collectionCount = collectionRepository.countByPoId(poLongId);
        if (collectionCount > 0) {
            blockers.add("수금 " + collectionCount + "건");
        }

        if (!blockers.isEmpty()) {
            return String.join(", ", blockers) + "이(가) 존재하여 삭제할 수 없습니다.";
        }
        return null;
    }
}
