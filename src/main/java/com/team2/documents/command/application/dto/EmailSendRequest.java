package com.team2.documents.command.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "이메일 발송 요청")
public record EmailSendRequest(
        @Schema(description = "거래처 ID") @NotNull Long clientId,
        @Schema(description = "PO 문서 코드") String poId,
        @Schema(description = "이메일 제목") @NotBlank String emailTitle,
        @Schema(description = "수신자 이름") String emailRecipientName,
        @Schema(description = "수신자 이메일") @NotBlank String emailRecipientEmail,
        @Schema(description = "첨부할 문서 유형 목록 (PI, PO, CI, PL, SHIPPING_ORDER, PRODUCTION_ORDER)")
        @NotNull List<String> docTypes,
        @Schema(description = "클라이언트에서 미리보기 HTML 기반으로 렌더링한 PDF 첨부. 비어 있으면 서버가 기존 방식으로 생성")
        List<EmailAttachmentRequest> attachments
) {}
