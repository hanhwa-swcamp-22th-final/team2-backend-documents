package com.team2.documents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.team2.documents.command.application.dto.ShipmentStatusUpdateRequest;
import com.team2.documents.command.domain.entity.Shipment;
import com.team2.documents.command.domain.entity.enums.ShipmentStatus;
import com.team2.documents.command.domain.repository.ShipmentRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@org.springframework.security.test.context.support.WithMockUser(username = "test-admin", roles = {"ADMIN"})
class ShipmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM shipments");
    }

    @Test
    @DisplayName("출하현황 상태 변경 API는 실제 DB 상태를 변경한다")
    void updateShipmentStatus_updatesShipmentInH2() throws Exception {
        Shipment shipment = shipmentRepository.save(new Shipment(1L, "PO260001", ShipmentStatus.READY));

        mockMvc.perform(put("/api/shipments/{id}", shipment.getShipmentId())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ShipmentStatusUpdateRequest(ShipmentStatus.COMPLETED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipmentStatus").value("COMPLETED"));

        Shipment updatedShipment = shipmentRepository.findById(shipment.getShipmentId()).orElseThrow();
        assertThat(updatedShipment.getShipmentStatus()).isEqualTo(ShipmentStatus.COMPLETED);
    }
}
