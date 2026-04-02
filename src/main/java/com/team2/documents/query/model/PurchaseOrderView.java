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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PurchaseOrderItemView> items = new ArrayList<>();
}
