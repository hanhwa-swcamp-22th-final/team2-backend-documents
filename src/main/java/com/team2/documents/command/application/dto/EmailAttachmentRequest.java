package com.team2.documents.command.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이메일 첨부 파일 요청")
public record EmailAttachmentRequest(
        @Schema(description = "첨부 파일명") String filename,
        @Schema(description = "MIME 타입", example = "application/pdf") String contentType,
        @Schema(description = "Base64 인코딩된 파일 데이터") String contentBase64
) {
}
