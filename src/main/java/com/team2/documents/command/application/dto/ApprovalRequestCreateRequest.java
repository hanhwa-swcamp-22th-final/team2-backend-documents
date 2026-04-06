package com.team2.documents.command.application.dto;

import com.team2.documents.command.domain.entity.enums.ApprovalDocumentType;
import com.team2.documents.command.domain.entity.enums.ApprovalRequestType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결재 요청 생성 요청 DTO")
public record ApprovalRequestCreateRequest(
        @Schema(description = "문서 유형 (PI, PO 등)", example = "PO")
        ApprovalDocumentType documentType,
        @Schema(description = "문서 ID", example = "PO-2026-0001")
        String documentId,
        @Schema(description = "요청 유형 (등록, 수정, 삭제)", example = "REGISTRATION")
        ApprovalRequestType requestType,
        @Schema(description = "요청자 ID", example = "1")
        Long requesterId,
        @Schema(description = "결재자 ID", example = "2")
        Long approverId,
        @Schema(description = "요청 코멘트", example = "등록 승인 요청드립니다.")
        String comment) {
}
