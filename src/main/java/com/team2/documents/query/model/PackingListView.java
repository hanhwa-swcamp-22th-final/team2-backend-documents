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
public class PackingListView {
    private Long packingListId;
    private String plId;
    private Long poId;
    private LocalDate invoiceDate;
    private Integer clientId;
    private BigDecimal grossWeight;
    private String status;
    private LocalDateTime createdAt;
}
