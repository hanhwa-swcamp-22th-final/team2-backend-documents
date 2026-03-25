package com.team2.documents.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team2.documents.dto.PurchaseOrderCreateRequest;
import com.team2.documents.dto.PurchaseOrderCreateResponse;
import com.team2.documents.dto.PurchaseOrderInitialStatusResponse;
import com.team2.documents.dto.ProformaInvoiceRegistrationRequest;
import com.team2.documents.dto.ProformaInvoiceRegistrationResponse;
import com.team2.documents.service.ProformaInvoiceService;
import com.team2.documents.service.PurchaseOrderModificationService;
import com.team2.documents.service.PurchaseOrderCreationService;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final PurchaseOrderModificationService purchaseOrderModificationService;
    private final PurchaseOrderCreationService purchaseOrderCreationService;
    private final ProformaInvoiceService proformaInvoiceService;

    public DocumentController(PurchaseOrderModificationService purchaseOrderModificationService,
                              PurchaseOrderCreationService purchaseOrderCreationService,
                              ProformaInvoiceService proformaInvoiceService) {
        this.purchaseOrderModificationService = purchaseOrderModificationService;
        this.purchaseOrderCreationService = purchaseOrderCreationService;
        this.proformaInvoiceService = proformaInvoiceService;
    }

    @PostMapping("/purchase-orders")
    public ResponseEntity<PurchaseOrderCreateResponse> create(@RequestBody PurchaseOrderCreateRequest request) {
        purchaseOrderCreationService.create(request.userId());
        return ResponseEntity.ok(new PurchaseOrderCreateResponse("PO 생성 요청이 처리되었습니다."));
    }

    @PostMapping("/proforma-invoices/request-registration")
    public ResponseEntity<ProformaInvoiceRegistrationResponse> requestRegistration(
            @RequestBody ProformaInvoiceRegistrationRequest request) {
        proformaInvoiceService.requestRegistration(request.piId(), request.userId());
        return ResponseEntity.ok(new ProformaInvoiceRegistrationResponse("PI 등록 요청이 처리되었습니다."));
    }

    @GetMapping("/purchase-orders/initial-status/{userId}")
    public ResponseEntity<PurchaseOrderInitialStatusResponse> determineInitialStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(new PurchaseOrderInitialStatusResponse(
                purchaseOrderCreationService.determineInitialStatus(userId)));
    }

    @PostMapping("/purchase-orders/{poId}/validate-modifiable")
    public ResponseEntity<Void> validateModifiable(@PathVariable String poId) {
        purchaseOrderModificationService.validateModifiable(poId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/purchase-orders/{poId}/validate-deletable")
    public ResponseEntity<Void> validateDeletable(@PathVariable String poId) {
        purchaseOrderModificationService.validateDeletable(poId);
        return ResponseEntity.ok().build();
    }
}
