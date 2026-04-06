package com.team2.documents.command.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pi_items")
public class ProformaInvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pi_item_id")
    private Long piItemId;

    @Column(name = "pi_id", insertable = false, updatable = false)
    private Long piId;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "pi_item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "pi_item_qty")
    private Integer quantity;

    @Column(name = "pi_item_unit", length = 20)
    private String unit;

    @Column(name = "pi_item_unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "pi_item_amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "pi_item_remark", columnDefinition = "TEXT")
    private String remark;

    protected ProformaInvoiceItem() {
    }

    public ProformaInvoiceItem(Integer itemId,
                               String itemName,
                               Integer quantity,
                               String unit,
                               BigDecimal unitPrice,
                               BigDecimal amount,
                               String remark) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.remark = remark;
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
