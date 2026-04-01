package com.team2.documents.command.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DocumentJsonSupportService {

    private static final TypeReference<List<Map<String, Object>>> LIST_OF_MAPS = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public DocumentJsonSupportService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String emptyArray() {
        return "[]";
    }

    public String createRevisionHistory(String action,
                                        Long actorUserId,
                                        String status,
                                        String message,
                                        LocalDateTime at) {
        return appendRevision("[]", action, actorUserId, status, message, at, Map.of());
    }

    public String appendRevision(String revisionHistory,
                                 String action,
                                 Long actorUserId,
                                 String status,
                                 String message,
                                 LocalDateTime at,
                                 Map<String, Object> changes) {
        List<Map<String, Object>> history = parseList(revisionHistory);
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("action", action);
        entry.put("actorUserId", actorUserId);
        entry.put("status", status);
        entry.put("message", message);
        entry.put("at", (at == null ? LocalDateTime.now() : at).toString());
        if (changes != null && !changes.isEmpty()) {
            entry.put("changes", changes);
        }
        history.add(entry);
        return write(history);
    }

    public String appendLinkedDocument(String linkedDocuments,
                                       String documentId,
                                       String documentType,
                                       String status) {
        List<Map<String, Object>> links = parseList(linkedDocuments);
        boolean exists = links.stream()
                .anyMatch(link -> Objects.equals(documentId, link.get("id"))
                        && Objects.equals(documentType, link.get("type")));
        if (!exists) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", documentId);
            entry.put("type", documentType);
            entry.put("status", status);
            links.add(entry);
        }
        return write(links);
    }

    public Map<String, Object> diffSnapshots(Map<String, Object> beforeSnapshot, Map<String, Object> afterSnapshot) {
        Map<String, Object> diff = new LinkedHashMap<>();
        Map<String, Object> before = beforeSnapshot == null ? Map.of() : beforeSnapshot;
        Map<String, Object> after = afterSnapshot == null ? Map.of() : afterSnapshot;

        java.util.Set<String> keys = new java.util.LinkedHashSet<>();
        keys.addAll(before.keySet());
        keys.addAll(after.keySet());

        for (String key : keys) {
            Object beforeValue = before.get(key);
            Object afterValue = after.get(key);
            if (!Objects.equals(beforeValue, afterValue)) {
                Map<String, Object> change = new LinkedHashMap<>();
                change.put("before", beforeValue);
                change.put("after", afterValue);
                diff.put(key, change);
            }
        }
        return diff;
    }

    private List<Map<String, Object>> parseList(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return new ArrayList<>(objectMapper.readValue(json, LIST_OF_MAPS));
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }

    private String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("문서 JSON 생성에 실패했습니다.", exception);
        }
    }
}
