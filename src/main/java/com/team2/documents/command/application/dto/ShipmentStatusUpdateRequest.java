package com.team2.documents.command.application.dto;

import com.team2.documents.command.domain.entity.enums.ShipmentStatus;

public record ShipmentStatusUpdateRequest(ShipmentStatus status) {
}
