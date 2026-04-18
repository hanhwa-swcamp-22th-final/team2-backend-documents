package com.team2.documents.query.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProformaInvoiceItemView {
    // pi_items 의 PK. MyBatis collection row 식별자로 사용. item_id (FK) 와 구분.
    private Long piItemId;
    private Integer itemId;
    private String itemName;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String remark;
}
