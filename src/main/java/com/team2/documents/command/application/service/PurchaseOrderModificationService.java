package com.team2.documents.command.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import com.team2.documents.command.domain.repository.CollectionRepository;
import com.team2.documents.command.domain.repository.CommercialInvoiceJpaRepository;
import com.team2.documents.command.domain.repository.PackingListJpaRepository;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.ShipmentRepository;

@Service
@Transactional
public class PurchaseOrderModificationService {

    private final ShipmentCommandService shipmentCommandService;
    private final PurchaseOrderCommandService purchaseOrderCommandService;
    private final CommercialInvoiceJpaRepository commercialInvoiceJpaRepository;
    private final PackingListJpaRepository packingListJpaRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final ShipmentRepository shipmentRepository;
    private final CollectionRepository collectionRepository;

    public PurchaseOrderModificationService(ShipmentCommandService shipmentCommandService,
                                            PurchaseOrderCommandService purchaseOrderCommandService,
                                            CommercialInvoiceJpaRepository commercialInvoiceJpaRepository,
                                            PackingListJpaRepository packingListJpaRepository,
                                            ProductionOrderRepository productionOrderRepository,
                                            ShipmentRepository shipmentRepository,
                                            CollectionRepository collectionRepository) {
        this.shipmentCommandService = shipmentCommandService;
        this.purchaseOrderCommandService = purchaseOrderCommandService;
        this.commercialInvoiceJpaRepository = commercialInvoiceJpaRepository;
        this.packingListJpaRepository = packingListJpaRepository;
        this.productionOrderRepository = productionOrderRepository;
        this.shipmentRepository = shipmentRepository;
        this.collectionRepository = collectionRepository;
    }

    public void validateModifiable(String poId) {
        Shipment shipment = shipmentCommandService.findByPoId(poId);

        if (ShipmentStatus.COMPLETED.equals(shipment.getShipmentStatus())) {
            throw new IllegalStateException("출하완료 상태의 PO는 수정할 수 없습니다.");
        }
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
