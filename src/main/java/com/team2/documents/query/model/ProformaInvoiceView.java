package com.team2.documents.query.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProformaInvoiceView {
    private Long proformaInvoiceId;
    private String piId;
    private String status;
    private LocalDate issueDate;
    private Integer clientId;
    private Integer currencyId;
    private Long managerId;
    private LocalDate deliveryDate;
    private String incotermsCode;
    private String namedPlace;
    private BigDecimal totalAmount;
    private String clientName;
    private String clientAddress;
    private String country;
    private String currencyCode;
    private String managerName;
    private String approvalStatus;
    private String requestStatus;
    private String approvalAction;
    private String approvalRequestedBy;
    private LocalDateTime approvalRequestedAt;
    private String approvalReview;
    private String itemsSnapshot;
    private String linkedDocuments;
    private String remarks;
    /** 거래처 바이어(PIC) 이름 스냅샷. PI 등록 시 buyer 드롭다운 선택값 (Issue C). */
    private String buyerName;
    private List<ProformaInvoiceItemView> items = new ArrayList<>();
}
