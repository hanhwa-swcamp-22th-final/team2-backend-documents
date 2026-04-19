package com.team2.documents.query.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductionOrderView {
    private Long productionOrderPk;
    private String productionOrderCode;
    private String productionOrderId;
    private Long purchaseOrderId;
    private String poId;
    private Integer clientId;
    private Long managerId;
    private String poNo;
    private LocalDate orderDate;
    private LocalDate dueDate;
    private String status;
    private String clientName;
    private String country;
    private String managerName;
    private String itemName;
    private String linkedDocuments;
    private String itemsSnapshot;
    private List<String> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
