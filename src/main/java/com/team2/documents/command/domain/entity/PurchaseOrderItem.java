package com.team2.documents.command.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;

@Setter
@Entity
@Table(name = "po_items")
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "po_item_id")
    private Long poItemId;

    @Column(name = "po_id", insertable = false, updatable = false)
    private Long poId;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "po_item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "po_item_qty", nullable = false)
    private Integer quantity;

    @Column(name = "po_item_unit", length = 20)
    private String unit;

    @Column(name = "po_item_unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "po_item_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "po_item_remark", columnDefinition = "TEXT")
    private String remark;

    // Issue D — master.items.item_weight(kg) 를 PO 생성 시점에 스냅샷으로 캡처.
    // PL 자동생성 시 SUM(qty × weight) 로 pl_gross_weight 를 계산한다. master 쪽
    // weight 가 이후 변경돼도 문서 시점 값이 보존되도록 컬럼에 저장.
    @Column(name = "po_item_weight", precision = 10, scale = 3)
    private BigDecimal itemWeight;

    protected PurchaseOrderItem() {
    }

    public PurchaseOrderItem(Integer itemId,
                             String itemName,
                             Integer quantity,
                             String unit,
                             BigDecimal unitPrice,
                             BigDecimal amount,
                             String remark) {
        this(itemId, itemName, quantity, unit, unitPrice, amount, remark, null);
    }

    public PurchaseOrderItem(Integer itemId,
                             String itemName,
                             Integer quantity,
                             String unit,
                             BigDecimal unitPrice,
                             BigDecimal amount,
                             String remark,
                             BigDecimal itemWeight) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity == null ? 0 : quantity;
        this.unit = unit;
        this.unitPrice = unitPrice == null ? BigDecimal.ZERO : unitPrice;
        this.amount = amount == null ? BigDecimal.ZERO : amount;
        this.remark = remark;
        this.itemWeight = itemWeight;
    }

    public Long getPoItemId() {
        return poItemId;
    }

    public Long getPoId() {
        return poId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getRemark() {
        return remark;
    }

    public BigDecimal getItemWeight() {
        return itemWeight;
    }
}
