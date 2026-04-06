package com.team2.documents.command.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이메일 로그 내부 요청 (Activity 서비스 연동)")
public record EmailLogInternalRequest(
        @Schema(description = "거래처 ID") Long clientId,
        @Schema(description = "PO 문서 코드") String poId,
        @Schema(description = "이메일 제목") String emailTitle,
        @Schema(description = "수신자 이름") String emailRecipientName,
        @Schema(description = "수신자 이메일") String emailRecipientEmail,
        @Schema(description = "발송자 사용자 ID") Long emailSenderId,
        @Schema(description = "발송 상태") String emailStatus,
        @Schema(description = "첨부 문서 유형 목록") List<String> docTypes,
        @Schema(description = "S3 키 목록") List<String> s3Keys,
        @Schema(description = "첨부 파일명 목록") List<String> attachmentFilenames
) {}
