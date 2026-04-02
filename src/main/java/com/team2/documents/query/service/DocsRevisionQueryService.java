package com.team2.documents.query.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.query.mapper.DocsRevisionQueryMapper;

@Service
public class DocsRevisionQueryService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final DocsRevisionQueryMapper docsRevisionQueryMapper;
    private final ObjectMapper objectMapper;

    public DocsRevisionQueryService(DocsRevisionQueryMapper docsRevisionQueryMapper, ObjectMapper objectMapper) {
        this.docsRevisionQueryMapper = docsRevisionQueryMapper;
        this.objectMapper = objectMapper;
    }

    public String getRevisionHistory(String docType, Long docId) {
        if (docId == null) {
            return "[]";
        }
        List<Map<String, Object>> history = new ArrayList<>();
        docsRevisionQueryMapper.findRevisionPayloadsByDocTypeAndDocId(docType, docId)
                .forEach(payload -> {
                    try {
                        Map<String, Object> parsed = objectMapper.readValue(payload, MAP_TYPE);
                        if ("REVISION".equals(parsed.get("entryType"))) {
                            history.add(parsed);
                        }
                    } catch (Exception ignored) {
                    }
                });
        try {
            return objectMapper.writeValueAsString(history);
        } catch (Exception exception) {
            return "[]";
        }
    }
}
