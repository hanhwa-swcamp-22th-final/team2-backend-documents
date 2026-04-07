package com.team2.documents.command.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentLinkBuilderServiceTest {

    @Mock
    private DocumentJsonSupportService documentJsonSupportService;

    @InjectMocks
    private DocumentLinkBuilderService documentLinkBuilderService;

    @Test
    @DisplayName("문서 링크 append는 JSON 지원 서비스에 위임한다")
    void append_whenCalled_thenDelegatesToJsonSupportService() {
        when(documentJsonSupportService.appendLinkedDocument("[]", "PI260001", "PI", "CONFIRMED"))
                .thenReturn("[{\"id\":\"PI260001\"}]");

        String result = documentLinkBuilderService.append("[]", "PI260001", "PI", "CONFIRMED");

        assertEquals("[{\"id\":\"PI260001\"}]", result);
        verify(documentJsonSupportService).appendLinkedDocument("[]", "PI260001", "PI", "CONFIRMED");
    }
}
