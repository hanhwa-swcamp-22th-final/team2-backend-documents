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
    private String clientName;
    private String clientAddress;
    private String country;
    private String currencyCode;
    private String paymentTerms;
    private String portOfDischarge;
    private String buyer;
    private String itemsSnapshot;
    private String linkedDocuments;
    private LocalDateTime createdAt;
    // purchase_orders JOIN 으로 가져오는 발행 메타 (스키마 동일: team2_docs)
    private String incotermsCode;
    private String namedPlace;
    private LocalDate deliveryDate;
}
