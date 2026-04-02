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
public class CollectionView {
    private Long collectionId;
    private String poId;
    private String poNo;
    private Long clientId;
    private String clientName;
    private BigDecimal totalAmount;
    private BigDecimal collectedAmount;
    private BigDecimal remainingAmount;
    private String currencyCode;
    private String status;
    private LocalDate collectionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
