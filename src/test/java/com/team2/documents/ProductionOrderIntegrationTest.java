package com.team2.documents;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductionOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM production_orders");
        jdbcTemplate.execute("DELETE FROM purchase_orders");
    }

    @Test
    @DisplayName("생산지시서 목록 조회 API는 H2에 저장된 데이터를 반환한다")
    void getProductionOrders_returnsPersistedProductionOrders() throws Exception {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(new PurchaseOrder("PO260003", PurchaseOrderStatus.DRAFT));
        productionOrderRepository.save(new ProductionOrder(
                "MO260001",
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                LocalDate.of(2026, 3, 30),
                LocalDate.of(2026, 4, 5),
                "진행중",
                List.of("item-1"),
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        mockMvc.perform(get("/api/production-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productionOrderId").value("MO260001"))
                .andExpect(jsonPath("$[0].poId").value("PO260003"))
                .andExpect(jsonPath("$[0].status").value("진행중"));
    }
}
