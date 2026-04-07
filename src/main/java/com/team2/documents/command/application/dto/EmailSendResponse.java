package com.team2.documents.command.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이메일 발송 응답")
public record EmailSendResponse(
        @Schema(description = "발송 상태 (SENT / FAILED)") String status,
        @Schema(description = "결과 메시지") String message,
        @Schema(description = "첨부 파일명 목록") List<String> attachmentFilenames
) {}
