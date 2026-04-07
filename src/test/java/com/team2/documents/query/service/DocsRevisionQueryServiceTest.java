package com.team2.documents.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.query.mapper.DocsRevisionQueryMapper;

@ExtendWith(MockitoExtension.class)
class DocsRevisionQueryServiceTest {

    @Mock
    private DocsRevisionQueryMapper docsRevisionQueryMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("문서 ID가 null이면 빈 배열 문자열을 반환한다")
    void getRevisionHistory_whenDocIdIsNull_thenReturnsEmptyArray() {
        DocsRevisionQueryService docsRevisionQueryService = new DocsRevisionQueryService(docsRevisionQueryMapper, objectMapper);
        String result = docsRevisionQueryService.getRevisionHistory("PO", null);

        assertEquals("[]", result);
    }

    @Test
    @DisplayName("REVISION 타입 이력만 필터링해 JSON 배열로 반환한다")
    void getRevisionHistory_whenPayloadsContainSnapshots_thenReturnsOnlyRevisionEntries() {
        when(docsRevisionQueryMapper.findRevisionPayloadsByDocTypeAndDocId("PO", 1L))
                .thenReturn(List.of(
                        "{\"entryType\":\"SNAPSHOT\",\"status\":\"DRAFT\"}",
                        "{\"entryType\":\"REVISION\",\"action\":\"REQUEST_REGISTRATION\"}",
                        "{\"entryType\":\"REVISION\",\"action\":\"APPROVED\"}"
                ));

        DocsRevisionQueryService docsRevisionQueryService = new DocsRevisionQueryService(docsRevisionQueryMapper, objectMapper);
        String result = docsRevisionQueryService.getRevisionHistory("PO", 1L);

        assertEquals("[{\"entryType\":\"REVISION\",\"action\":\"REQUEST_REGISTRATION\"},{\"entryType\":\"REVISION\",\"action\":\"APPROVED\"}]",
                result);
    }

    @Test
    @DisplayName("파싱 실패 payload는 무시하고 직렬화 실패 시 빈 배열을 반환한다")
    void getRevisionHistory_whenPayloadIsInvalid_thenSkipsInvalidEntries() {
        when(docsRevisionQueryMapper.findRevisionPayloadsByDocTypeAndDocId("PO", 2L))
                .thenReturn(List.of(
                        "not-json",
                        "{\"entryType\":\"REVISION\",\"action\":\"REJECTED\"}"
                ));

        DocsRevisionQueryService docsRevisionQueryService = new DocsRevisionQueryService(docsRevisionQueryMapper, objectMapper);
        String result = docsRevisionQueryService.getRevisionHistory("PO", 2L);

        assertEquals("[{\"entryType\":\"REVISION\",\"action\":\"REJECTED\"}]", result);
    }
}
