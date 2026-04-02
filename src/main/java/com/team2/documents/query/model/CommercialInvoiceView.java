package com.team2.documents.query.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommercialInvoiceView {
    private Long commercialInvoiceId;
    private String ciId;
    private Long poId;
    private LocalDate invoiceDate;
    private Integer clientId;
    private Integer currencyId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
