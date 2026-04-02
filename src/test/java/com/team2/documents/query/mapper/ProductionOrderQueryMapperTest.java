package com.team2.documents.query.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.team2.documents.command.domain.entity.ProductionOrder;
import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.ProductionOrderRepository;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;
import com.team2.documents.query.model.ProductionOrderView;

@SpringBootTest
@ActiveProfiles("test")
class ProductionOrderQueryMapperTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductionOrderRepository productionOrderRepository;

    @Autowired
    private ProductionOrderQueryMapper productionOrderQueryMapper;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM production_orders");
        jdbcTemplate.execute("DELETE FROM purchase_orders");
    }

    @Test
    @DisplayName("ProductionOrderQueryMapper는 실제 DB에서 production code와 po code를 함께 매핑한다")
    void findById_mapsProductionOrderCodesFromDatabase() {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.save(new PurchaseOrder("PO260001", PurchaseOrderStatus.CONFIRMED));
        productionOrderRepository.save(new ProductionOrder(
                "MO260001",
                purchaseOrder.getPurchaseOrderId(),
                purchaseOrder.getPoId(),
                LocalDate.of(2026, 4, 2),
                LocalDate.of(2026, 4, 20),
                "진행중",
                java.util.List.of(),
                null,
                null
        ));

        ProductionOrderView result = productionOrderQueryMapper.findById("MO260001");

        assertThat(result).isNotNull();
        assertThat(result.getProductionOrderCode()).isEqualTo("MO260001");
        assertThat(result.getProductionOrderId()).isEqualTo("MO260001");
        assertThat(result.getPurchaseOrderId()).isEqualTo(purchaseOrder.getPurchaseOrderId());
        assertThat(result.getPoId()).isEqualTo("PO260001");
        assertThat(result.getPoNo()).isEqualTo("PO260001");
        assertThat(result.getStatus()).isEqualTo("진행중");
    }
}
