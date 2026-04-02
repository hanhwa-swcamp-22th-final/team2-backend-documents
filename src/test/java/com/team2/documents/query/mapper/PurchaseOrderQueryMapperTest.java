package com.team2.documents.query.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.team2.documents.command.domain.entity.PurchaseOrder;
import com.team2.documents.command.domain.entity.PurchaseOrderItem;
import com.team2.documents.command.domain.entity.enums.PurchaseOrderStatus;
import com.team2.documents.command.domain.repository.PurchaseOrderRepository;
import com.team2.documents.command.domain.repository.UserPositionRepository;
import com.team2.documents.query.model.PurchaseOrderView;

@SpringBootTest
@ActiveProfiles("test")
class PurchaseOrderQueryMapperTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderQueryMapper purchaseOrderQueryMapper;

    @MockitoBean
    private UserPositionRepository userPositionRepository;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM po_items");
        jdbcTemplate.execute("DELETE FROM purchase_orders");
    }

    @Test
    @DisplayName("PurchaseOrderQueryMapper는 실제 DB에서 PO 본문과 품목 컬렉션을 함께 매핑한다")
    void findById_mapsPurchaseOrderAndItemsFromDatabase() {
        PurchaseOrder purchaseOrder = new PurchaseOrder(
                "PO260001",
                "PI260001",
                LocalDate.of(2026, 4, 2),
                10,
                1,
                2L,
                PurchaseOrderStatus.DRAFT,
                LocalDate.of(2026, 4, 20),
                "FOB",
                "Busan",
                LocalDate.of(2026, 4, 18),
                false,
                new BigDecimal("30.00"),
                "ABC Trading",
                "Seoul",
                "KR",
                "USD",
                "Kim",
                "대기",
                "등록요청",
                "등록",
                "Kim",
                null,
                null,
                "[{\"itemName\":\"Bolt\"}]",
                "[]",
                List.of(
                        new PurchaseOrderItem(100, "Bolt", 3, "EA", new BigDecimal("10.00"), new BigDecimal("30.00"), "urgent")
                )
        );

        PurchaseOrder saved = purchaseOrderRepository.save(purchaseOrder);

        PurchaseOrderView result = purchaseOrderQueryMapper.findById("PO260001");

        assertThat(result).isNotNull();
        assertThat(result.getPurchaseOrderId()).isEqualTo(saved.getPurchaseOrderId());
        assertThat(result.getPoId()).isEqualTo("PO260001");
        assertThat(result.getPiId()).isEqualTo("PI260001");
        assertThat(result.getStatus()).isEqualTo("DRAFT");
        assertThat(result.getClientName()).isEqualTo("ABC Trading");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getItemName()).isEqualTo("Bolt");
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(3);
        assertThat(result.getItems().get(0).getAmount()).isEqualByComparingTo("30.00");
    }
}
