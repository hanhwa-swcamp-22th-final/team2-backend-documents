package com.team2.documents.query.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrderView {
    private Long purchaseOrderId;
    private String poId;
    private String piId;
    private LocalDate issueDate;
    private Integer clientId;
    private Integer currencyId;
    private Long managerId;
    private String status;
    private LocalDate deliveryDate;
    private String incotermsCode;
    private String namedPlace;
    private LocalDate sourceDeliveryDate;
    private boolean deliveryDateOverride;
    private BigDecimal totalAmount;
    private String clientName;
    private String clientAddress;
    private String country;
    private String currencyCode;
    private String managerName;
    private String approvalStatus;
    private String requestStatus;
    private String approvalAction;
    private String approvalRequestedBy;
    private LocalDateTime approvalRequestedAt;
    private String approvalReview;
    private String itemsSnapshot;
    private String linkedDocuments;
    private String remarks;
    /** 거래처 바이어(PIC) 이름 스냅샷. PI 에서 승계 (Issue C). */
    private String buyerName;
    /** PO 별 출하 진행 상태 aggregate. null=출하전, 'preparing'=일부 출하, 'completed'=모두 완료. */
    private String shipmentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PurchaseOrderItemView> items = new ArrayList<>();
}
