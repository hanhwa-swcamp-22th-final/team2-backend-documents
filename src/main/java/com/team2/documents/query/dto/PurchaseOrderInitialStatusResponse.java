package com.team2.documents.query.dto;

import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Purchase Order 초기 상태 조회 응답 DTO")
public record PurchaseOrderInitialStatusResponse(
        @Schema(description = "사용자 직급에 따른 PO 초기 상태", example = "DRAFT")
        PurchaseOrderStatus status) {
}
