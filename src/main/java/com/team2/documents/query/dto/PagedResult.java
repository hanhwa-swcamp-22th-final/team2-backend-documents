package com.team2.documents.query.dto;

import java.util.List;

public record PagedResult<T>(List<T> content, long totalElements) {
}
