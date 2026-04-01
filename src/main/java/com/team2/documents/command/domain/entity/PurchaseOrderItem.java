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

    @Column(name = "po_id", insertable = false, updatable = false, length = 30)
    private String poId;

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

    protected PurchaseOrderItem() {
    }

    public PurchaseOrderItem(Integer itemId,
                             String itemName,
                             Integer quantity,
                             String unit,
                             BigDecimal unitPrice,
                             BigDecimal amount,
                             String remark) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity == null ? 0 : quantity;
        this.unit = unit;
        this.unitPrice = unitPrice == null ? BigDecimal.ZERO : unitPrice;
        this.amount = amount == null ? BigDecimal.ZERO : amount;
        this.remark = remark;
    }

    public Long getPoItemId() {
        return poItemId;
    }

    public String getPoId() {
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
}
