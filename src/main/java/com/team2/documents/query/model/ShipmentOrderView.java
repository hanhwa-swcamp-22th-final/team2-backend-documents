package com.team2.documents.query.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentOrderView {
    private Long shipmentOrderPk;
    private String shipmentOrderId;
    private Long purchaseOrderId;
    private String poId;
    private LocalDate issueDate;
    private Integer clientId;
    private Long managerId;
    private String status;
    private LocalDate dueDate;
    private String clientName;
    private String country;
    private String managerName;
    private String itemName;
    private String linkedDocuments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
