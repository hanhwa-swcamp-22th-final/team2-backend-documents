package com.team2.documents.command.application.dto;

import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출하 상태 변경 요청 DTO")
public record ShipmentStatusUpdateRequest(
        @Schema(description = "변경할 출하 상태", example = "SHIPPED")
        ShipmentStatus status) {
}
