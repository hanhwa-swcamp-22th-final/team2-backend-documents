package com.team2.documents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.documents.command.application.dto.CollectionUpdateRequest;
import com.team2.documents.command.domain.entity.Collection;
import com.team2.documents.command.domain.repository.CollectionRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@org.springframework.security.test.context.support.WithMockUser(username = "test-admin", roles = {"ADMIN"})
class CollectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CollectionRepository collectionRepository;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM collections");
    }

    @Test
    @DisplayName("수금 처리 API는 실제 DB 상태와 수금일을 변경한다")
    void updateCollection_updatesCollectionInH2() throws Exception {
        Collection collection = collectionRepository.save(new Collection(
                null,
                "PO260002",
                "PO260002",
                10L,
                "ABC Trading",
                new BigDecimal("100000"),
                null,
                null,
                "USD",
                "미수금",
                null,
                null,
                null
        ));

        mockMvc.perform(put("/api/collections/{collectionId}", collection.getCollectionId())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CollectionUpdateRequest("수금완료", LocalDate.of(2026, 3, 30), "completed"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("수금완료"))
                .andExpect(jsonPath("$.collectionDate").value("2026-03-30"));

        Collection updatedCollection = collectionRepository.findById(collection.getCollectionId()).orElseThrow();
        assertThat(updatedCollection.getStatus()).isEqualTo("수금완료");
        assertThat(updatedCollection.getCollectionDate()).isEqualTo(LocalDate.of(2026, 3, 30));
    }
}
