package com.team2.documents.dto;

import com.team2.documents.entity.enums.ShipmentStatus;

public record ShipmentStatusUpdateRequest(ShipmentStatus status) {
}
