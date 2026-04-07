package com.team2.documents.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class DocumentJsonSupportServiceTest {

    private final DocumentJsonSupportService documentJsonSupportService =
            new DocumentJsonSupportService(new ObjectMapper());

    @Test
    @DisplayName("연결 문서 JSON에 없는 문서를 추가한다")
    void appendLinkedDocument_whenDocumentDoesNotExist_thenAppendsEntry() {
        String result = documentJsonSupportService.appendLinkedDocument(
                "[{\"id\":\"PI260001\",\"type\":\"PI\",\"status\":\"CONFIRMED\"}]",
                "CI260001",
                "CI",
                "CREATED"
        );

        assertThat(result).contains("\"id\":\"PI260001\"");
        assertThat(result).contains("\"id\":\"CI260001\"");
        assertThat(result).contains("\"type\":\"CI\"");
    }

    @Test
    @DisplayName("스냅샷 diff 계산 시 바뀐 필드만 before/after로 반환한다")
    void diffSnapshots_whenValuesChanged_thenReturnsChangedFieldsOnly() {
        Map<String, Object> beforeSnapshot = new LinkedHashMap<>();
        beforeSnapshot.put("status", "DRAFT");
        beforeSnapshot.put("totalAmount", 100);

        Map<String, Object> afterSnapshot = new LinkedHashMap<>();
        afterSnapshot.put("status", "CONFIRMED");
        afterSnapshot.put("totalAmount", 100);
        afterSnapshot.put("approvalStatus", "승인");

        Map<String, Object> diff = documentJsonSupportService.diffSnapshots(beforeSnapshot, afterSnapshot);

        assertThat(diff).containsKeys("status", "approvalStatus");
        assertThat(diff).doesNotContainKey("totalAmount");
        Map<String, Object> statusDiff = (Map<String, Object>) diff.get("status");
        assertThat(statusDiff.get("before")).isEqualTo("DRAFT");
        assertThat(statusDiff.get("after")).isEqualTo("CONFIRMED");
    }
}
