package com.team2.documents.query.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrderItemView {
    private Long poItemId;
    private Long poId;
    private Integer itemId;
    private String itemName;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String remark;
}
