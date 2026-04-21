package com.team2.documents.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.team2.documents.command.domain.entity.DocumentEmailOutbox;
import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.ProformaInvoice;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.ShipmentOrder;
import com.team2.documents.command.domain.repository.DocumentEmailOutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentAutoMailService {

    private final DocumentEmailOutboxRepository outboxRepository;
    private final DocumentEmailOutboxWorker outboxWorker;

    public void sendApprovedPiToBuyer(ProformaInvoice proformaInvoice) {
        if (proformaInvoice == null || isBlank(proformaInvoice.getPiId())) {
            log.warn("PI 자동 메일 outbox 적재 skip - PI 코드가 없습니다.");
            return;
        }
        enqueue(DocumentEmailOutbox.approvedPiToBuyer(proformaInvoice.getPiId()));
    }

    public void sendShipmentOrderToShippingTeam(PurchaseOrder purchaseOrder, ShipmentOrder shipmentOrder) {
        if (shipmentOrder == null || isBlank(shipmentOrder.getShipmentOrderId())) {
            log.warn("출하지시서 자동 메일 outbox 적재 skip - 출하지시서 코드가 없습니다.");
            return;
        }
        String poCode = purchaseOrder == null ? null : purchaseOrder.getPoId();
        enqueue(DocumentEmailOutbox.shipmentOrderToShippingTeam(shipmentOrder.getShipmentOrderId(), poCode));
    }

    public void sendProductionOrderToProductionTeam(PurchaseOrder purchaseOrder, ProductionOrder productionOrder) {
        if (productionOrder == null || isBlank(productionOrder.getProductionOrderId())) {
            log.warn("생산지시서 자동 메일 outbox 적재 skip - 생산지시서 코드가 없습니다.");
            return;
        }
        String poCode = purchaseOrder == null ? null : purchaseOrder.getPoId();
        enqueue(DocumentEmailOutbox.productionOrderToProductionTeam(productionOrder.getProductionOrderId(), poCode));
    }

    private void enqueue(DocumentEmailOutbox outbox) {
        DocumentEmailOutbox saved = outboxRepository.save(outbox);
        Runnable dispatch = () -> outboxWorker.processAsync(saved.getId());

        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    dispatch.run();
                }
            });
        } else {
            dispatch.run();
        }

        log.info("자동 메일 outbox 적재 완료. outboxId={}, type={}, documentCode={}",
                saved.getId(),
                saved.getEventType(),
                saved.getDocumentCode());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
